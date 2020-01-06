package blockchain.msg;

/**
 * 消息类型常量
 * @author fanbin
 * @date 2020/1/6
 */
public class Constant {

    private Constant() {

    }

    /**
     * 请求最新区块
     */
    public static final int REQ_LATEST_BLOCK = 0;

    /**
     * 响应最新区块
     */
    public static final int RES_LATEST_BLOCK = 1;

    /**
     * 请求整个区块链
     */
    public static final int REQ_BLOCK_CHAIN = 2;

    /**
     * 响应整个区块链
     */
    public static final int RES_BLOCK_CHAIN = 3;


}
