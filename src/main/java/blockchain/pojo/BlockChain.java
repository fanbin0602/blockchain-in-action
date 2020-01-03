package blockchain.pojo;

import blockchain.util.HashUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 区块链
 * @author fanbin
 * @date 2020/1/3
 */
public class BlockChain {

    /**
     * 难度
     */
    private static final int DIFFICULTY = 3;
    private static final char ZERO = '0';

    /**
     * 区块列表
     */
    private List<Block> blockChain;

    public List<Block> getBlockChain() {
        return blockChain;
    }

    /**
     * 构造方法
     */
    public BlockChain() {
        // 初始化区块列表
        this.blockChain = new ArrayList<Block>();
        // 向列表中添加第一个区块（创世区块）
        blockChain.add(this.createGenesisBlock());
    }

    /**
     * 创建创世区块
     * @return
     */
    private Block createGenesisBlock() {
        Block block = new Block();
        block.setIndex(0);
        block.setTimestamp(1578000000000L);
        block.setHash("000c81784691c7bb8bacab6affb23312fd707bf0463e9afc217ce1a32ab3a4aa");
        block.setPreviousHash("0");
        block.setData("GENESIS BLOCK");
        block.setNonce(1545);
        return block;
    }

    /**
     * 挖矿（计算 nonce 和哈希值）
     * @param index
     * @param previousHash
     * @param timestamp
     * @param data
     * @param nonce
     * @param block
     * @return
     */
    private String calculateNonceAndHash(int index, String previousHash, long timestamp,
                                         String data, long nonce, Block block) {
        while (true) {
            System.out.println("当前的 nonce 值是：" + nonce);
            String str = index + previousHash + timestamp + data + nonce;
            String hash = HashUtil.getSHA256(str);
            if (isValidHash(hash)) {
                System.out.println("找到了合法的哈希值：" + hash);
                block.setIndex(index); block.setPreviousHash(previousHash);
                block.setTimestamp(timestamp); block.setData(data);
                block.setNonce(nonce); block.setHash(hash);
                break;
            }
            nonce++;
        }
        return block.getHash();
    }
    // private static final int DIFFICULTY = 3;
    // private static final char ZERO = '0';

    /**
     * 验证哈希值是否合法
     * @param hash 哈希值
     * @return
     */
    private boolean isValidHash(String hash) {
        if (hash == null) { return false; }
        for (int i = 0; i < hash.length(); i++) {
            if (hash.charAt(i) != ZERO) {
                return i >= DIFFICULTY;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        Block block = new Block();
        block.setIndex(0);
        block.setTimestamp(1578000000000L);
        //block.setHash("000c81784691c7bb8bacab6affb23312fd707bf0463e9afc217ce1a32ab3a4aa");
        block.setPreviousHash("0");
        block.setData("GENESIS BLOCK");
        //block.setNonce(1545);

        BlockChain blockchain = new BlockChain();
        String hash = blockchain.calculateNonceAndHash(
                block.getIndex(), block.getPreviousHash(), block.getTimestamp(),
                block.getData(), 0, block
        );

    }

}
