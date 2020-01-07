package blockchain.http;

import blockchain.p2p.P2PNode;
import blockchain.pojo.BlockChain;
import com.alibaba.fastjson.JSON;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            resp.getWriter().println(JSON.toJSONString("Hello, world!"));
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
