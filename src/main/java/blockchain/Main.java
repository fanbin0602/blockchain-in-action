package blockchain;

import blockchain.http.HttpServer;
import blockchain.p2p.P2PNode;
import blockchain.pojo.Block;
import blockchain.pojo.BlockChain;
import com.alibaba.fastjson.JSON;

/**
 * @author fanbin
 * @date 2020/1/3
 */
public class Main {

    public static void main(String[] args) {

        /**
         * 三个参数：
         * 0 - http 服务的端口号
         * 1 - WebSocket 服务的端口号
         * 2 - （可选）启动后要连接的节点地址
         */

        if (args != null && (args.length == 2 || args.length == 3)) {
            try {
                //取出 http 和 WebSocket 端口号参数
                int httpPort = Integer.parseInt(args[0]);
                int wsPort = Integer.parseInt(args[1]);
                // 创建区块链对象
                BlockChain bc = new BlockChain();
                // 创建 P2P 节点
                P2PNode p2p = new P2PNode(bc);
                // 初始化 P2P 节点
                p2p.initNode(wsPort);
                // 如果参数包含了要连接的节点地址，就发起连接
                if (args.length == 3 && args[2] != null) {
                    p2p.connectToNode(args[2]);
                }
                // 创建 HTTP 服务
                HttpServer server = new HttpServer(p2p);
                // 初始化 HTTP 服务
                server.initServer(httpPort);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("参数错误");
        }



    }
}
