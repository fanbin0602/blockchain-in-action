package blockchain.http;

import blockchain.p2p.P2PNode;
import blockchain.pojo.Block;
import blockchain.pojo.BlockChain;
import com.alibaba.fastjson.JSON;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.java_websocket.WebSocket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 服务
 * @author fanbin
 * @date 2020/1/7
 */
public class HttpServer {

    /**
     * P2P 网络节点
     */
    private P2PNode p2p;

    /**
     * 区块链
     */
    private BlockChain blockChain;

    /**
     * 构造方法
     * @param p2p
     */
    public HttpServer(P2PNode p2p) {
        this.p2p = p2p;
        this.blockChain = p2p.getBlockChain();
    }

    public void initServer(int port) {
        try {
            Server server = new Server(port);
            System.out.println("监听 HTTP 端口号：" + port);
            ServletContextHandler context =
                    new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/block-chain");
            server.setHandler(context);

            context.addServlet(new ServletHolder(new HelloServlet()), "/hello");

            context.addServlet(new ServletHolder(new BlocksServlet()), "/blocks");
            context.addServlet(new ServletHolder(new MineBlockServlet()), "/mineBlock");
            context.addServlet(new ServletHolder(new AddPeerServlet()), "/addPeer");
            context.addServlet(new ServletHolder(new PeersServlet()), "/peers");

            server.start();
            server.join();
        } catch (Exception ex) {
            System.out.println("服务初始化异常");
            ex.printStackTrace();
        }
    }

    private class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            String str = "Hello, world!";
            String name = req.getParameter("name");
            if (name != null && name.length() > 0) {
                str = "Hello, " + name + "!";
            }
            resp.getWriter().println(JSON.toJSONString(str));
        }
    }

    /**
     * 查看区块列表数据
     */
    private class BlocksServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            String content = JSON.toJSONString(blockChain.getBlockChain());
            resp.getWriter().println(content);
        }
    }

    /**
     * 挖矿（生成新区块）
     */
    private class MineBlockServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            // 从请求参数中获取新区块中将要保存的数据
            String data = req.getParameter("data");
            // 生成新区块
            Block newBlock = blockChain.generateNextBlock(data);
            // 把生成的新区块追加到区块链末尾
            blockChain.addBlock(newBlock);
            // 向其他节点广播新区块
            p2p.broadcastLatestBlock();
            // 返回新区块的数据
            resp.getWriter().println(JSON.toJSONString(newBlock));
        }
    }

    /**
     * 连接新节点
     */
    private class AddPeerServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            // 从请求参数重获取节点的地址
            String peer = req.getParameter("peer");
            // 连接节点
            p2p.connectToNode(peer);
            // 返回成功状态
            resp.getWriter().println(JSON.toJSONString("ok"));
        }
    }

    /**
     * 节点查询列表
     */
    private class PeersServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            List<Map<String, String>> peers = new ArrayList<Map<String, String>>();
            for (WebSocket socket : p2p.getSockets()) {
                InetSocketAddress address = socket.getRemoteSocketAddress();
                Map<String, String> peerMap = new HashMap<String, String>();
                peerMap.put("remoteHost", address.getHostName());
                peerMap.put("remotePort", String.valueOf(address.getPort()));
                peers.add(peerMap);
            }
            String content = JSON.toJSONString(peers);
            resp.getWriter().println(content);
        }
    }

    public static void main(String[] args) {
        BlockChain bc = new BlockChain();
        P2PNode p2p = new P2PNode(bc);
        p2p.initNode(7001);
        HttpServer server = new HttpServer(p2p);
        server.initServer(8080);
    }














}
