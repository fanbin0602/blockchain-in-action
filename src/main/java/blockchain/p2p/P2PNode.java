package blockchain.p2p;

import blockchain.msg.Constant;
import blockchain.msg.Message;
import blockchain.pojo.Block;
import blockchain.pojo.BlockChain;
import com.alibaba.fastjson.JSON;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * P2P 网络节点
 * @author fanbin
 * @date 2020/1/6
 */
public class P2PNode {

    /**
     * 与本节点连接的其他节点的信息
     */
    private List<WebSocket> sockets;

    /**
     * 本地区块链
     */
    private BlockChain blockChain;

    public BlockChain getBlockChain() {
        return blockChain;
    }

    /**
     * 构造方法
     * @param blockChain
     */
    public P2PNode(BlockChain blockChain) {
        // 初始化节点列表
        sockets = new ArrayList<WebSocket>();
        //
        this.blockChain = blockChain;
    }

    /**
     * 初始化节点服务端并启动
     * @param port 端口号
     */
    public void initNode(int port) {

        final WebSocketServer server = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                System.out.println("server:被客户端连接：" + conn.getRemoteSocketAddress());
                sockets.add(conn);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                System.out.println("server:连接被关闭：" + conn.getRemoteSocketAddress());
                sockets.remove(conn);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                System.out.println("server:收到客户端的信息，来自：" + conn.getRemoteSocketAddress());
                System.out.println("server:收到客户端的信息，内容是：" + message);
                try {
                    handleMessage(conn, JSON.parseObject(message, Message.class));
                } catch (Exception ex) {
                    System.out.println("处理消息异常");
                    System.out.println("消息内容：" + message);
                    ex.printStackTrace();
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                System.out.println("server:连接发生错误：" + conn.getRemoteSocketAddress());
                sockets.remove(conn);
                ex.printStackTrace();
            }

            @Override
            public void onStart() {
                System.out.println("server:服务端已启动");
            }
        };
        server.start();
        System.out.println("服务已经启动，端口号是：" + port);

    }

    /**
     * 连接到远程节点
     * @param remote 远程节点的地址 ws://127.0.0.1:7001
     */
    public void connectToNode(String remote) {

        // 从 remote 当中获取主机地址
        String host = remote.split(":")[1].split("//")[1];

        for (WebSocket socket : sockets) {
            InetSocketAddress address = socket.getRemoteSocketAddress();
            if (address.getHostName().equals(host)) {
                return;
            }
        }

        try {

            final WebSocketClient client = new WebSocketClient(new URI(remote)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("client:连接服务端成功");
                    sockets.add(this);
                    this.send(reqLatestBlockMsg());
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("client:收到服务端的信息：" + message);
                    try {
                        handleMessage(this, JSON.parseObject(message, Message.class));
                    } catch (Exception ex) {
                        System.out.println("处理消息异常");
                        System.out.println("消息内容：" + message);
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("client:与服务端断开连接");
                    sockets.remove(this);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("client:与服务端连接出错");
                    sockets.remove(this);
                }
            };
            client.connect();
        } catch (Exception ex) {
            System.out.println("客户端初始化异常");
            ex.printStackTrace();
        }

    }

    /**
     * 广播消息
     * @param message 要广播的消息内容
     */
    private void broadcast(String message) {
        for (WebSocket socket : sockets) {
            socket.send(message);
        }
    }

    /**
     * 广播最新区块
     */
    private void broadcastLatestBlock() {
        broadcast(resLatestBlockMsg());
    }

    /**
     * 处理接收到的消息
     * @param socket 远程连接
     * @param message 收到的消息
     */
    private void handleMessage(WebSocket socket, Message message) {
        // 处理收到的消息
        System.out.println("开始处理收到的消息：" + JSON.toJSONString(message));
        switch (message.getType()) {
            // 收到请求最新区块链的消息
            case Constant.REQ_LATEST_BLOCK:
                socket.send(resLatestBlockMsg());
                break;
            // 收到请求整个区块列表的消息
            case Constant.REQ_BLOCK_CHAIN:
                socket.send(resBlockChainMsg());
                break;
            // 收到对方发来的区块数据（最新区块或整个区块列表）
            case Constant.RES_BLOCKS:
                handleBlockResponse(message.getData());
                break;
        }
    }

    /**
     * 处理收到的最新区块链的消息
     * @param data
     */
    private void handleBlockResponse(String data) {
        List<Block> blocksReceived = JSON.parseArray(data, Block.class);
        // 收到的最新区块
        Block latestBlockReceived = blocksReceived.get(blocksReceived.size() - 1);
        // 本地的最新区块
        Block latestBlock = blockChain.getLastBlock();
        // 判断收到的区块索引是否大于本地最新区块的索引，否则不处理
        if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
            // 判断能不能直接追加在本地区块链末尾
            if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())
                    && latestBlock.getIndex() + 1 == latestBlockReceived.getIndex()) {
                System.out.println("在本地区块链末尾追加接收到的新区块");
                blockChain.addBlock(latestBlockReceived);
                broadcastLatestBlock();
            } else if (blocksReceived.size() == 1) {
                // 如果收到的是最新区块，则请求整个区块列表
                System.out.println("向对方请求整个区块列表");
                broadcast(reqBlockChainMsg());
            } else {
                // 如果收到的是区块列表，则替换本地区块列表
                System.out.println("替换本地的区块列表");
                blockChain.replaceChain(blocksReceived);
                broadcastLatestBlock();
            }
        } else {
            System.out.println("对方的区块链不比本地的更长，不作处理");
        }
    }

    /**
     * 生成消息文本：请求最新区块
     * @return
     */
    private String reqLatestBlockMsg() {
        return JSON.toJSONString(new Message(Constant.REQ_LATEST_BLOCK));
    }

    /**
     * 生成消息文本：请求区块列表
     * @return
     */
    private String reqBlockChainMsg() {
        return JSON.toJSONString(new Message(Constant.REQ_BLOCK_CHAIN));
    }

    /**
     * 生成消息文本：响应最新区块
     * @return
     */
    private String resLatestBlockMsg() {
        Block[] block = {this.blockChain.getLastBlock()};
        String data = JSON.toJSONString(block);
        return JSON.toJSONString(new Message(Constant.RES_BLOCKS, data));
    }

    /**
     * 生成消息文本：响应区块列表
     * @return
     */
    private String resBlockChainMsg() {
        String data = JSON.toJSONString(this.blockChain.getBlockChain());
        return JSON.toJSONString(new Message(Constant.RES_BLOCKS, data));
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建区块链对象
        BlockChain bc = new BlockChain();
        // 创建P2P节点对象
        P2PNode p2p = new P2PNode(bc);
        // 初始化P2P节点（创建服务端并启动）
        p2p.initNode(7001);
        // 等待1秒钟
        // Thread.sleep(1000);
        // 创建客户端并向服务端发起连接
        p2p.connectToNode("ws://172.18.0.82:7000");

        // ws://172.18.0.82:7000



//        BlockChain bc = new BlockChain();
//        bc.addBlock(bc.generateNextBlock("hello"));
//        P2PNode p2p = new P2PNode(bc);
//
        System.out.println(p2p.reqLatestBlockMsg());
        System.out.println(p2p.resLatestBlockMsg());
        System.out.println(p2p.reqBlockChainMsg());
        System.out.println(p2p.resBlockChainMsg());

//        String remote = "ws://127.0.0.1:7001";
//        String host = remote.split(":")[1].split("//")[1];
//        System.out.println(host);


    }








}
