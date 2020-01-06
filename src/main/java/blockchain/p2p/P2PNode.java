package blockchain.p2p;

import blockchain.pojo.BlockChain;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
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

    public void initNode(int port) {

        final WebSocketServer server = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                System.out.println("被客户端连接：" + conn.getRemoteSocketAddress());
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                System.out.println("连接被关闭：" + conn.getRemoteSocketAddress());
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                System.out.println("收到客户端的信息，来自：" + conn.getRemoteSocketAddress());
                System.out.println("收到客户端的信息，内容是：" + message);
                conn.send("你刚才对我说：" + message);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                System.out.println("连接发生错误：" + conn.getRemoteSocketAddress());
                ex.printStackTrace();
            }

            @Override
            public void onStart() {
                System.out.println("服务端已启动");
            }
        };
        server.start();
        System.out.println("服务已经启动，端口号是：" + port);

    }

    public static void main(String[] args) {
        BlockChain bc = new BlockChain();
        P2PNode p2p = new P2PNode(bc);
        p2p.initNode(7001);
    }








}
