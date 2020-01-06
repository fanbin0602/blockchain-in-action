package blockchain.p2p;

import blockchain.pojo.BlockChain;
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
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                System.out.println("server:连接被关闭：" + conn.getRemoteSocketAddress());
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                System.out.println("server:收到客户端的信息，来自：" + conn.getRemoteSocketAddress());
                System.out.println("server:收到客户端的信息，内容是：" + message);
                conn.send("你刚才对我说：" + message);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                System.out.println("server:连接发生错误：" + conn.getRemoteSocketAddress());
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

        try {

            final WebSocketClient client = new WebSocketClient(new URI(remote)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("client:连接服务端成功");
                    this.send("你好啊");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("client:收到服务端的信息：" + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("client:与服务端断开连接");
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("client:与服务端连接出错");
                }
            };
            client.connect();
        } catch (Exception ex) {
            System.out.println("客户端初始化异常");
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        BlockChain bc = new BlockChain();
        P2PNode p2p = new P2PNode(bc);
        p2p.initNode(7001);
        Thread.sleep(1000);
        p2p.connectToNode("ws://127.0.0.1:7001");
    }








}
