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
        block.setPreviousHash("0");
        block.setData("GENESIS BLOCK");
        calculateNonceAndHash(block);
        return block;
    }

    /**
     * 生成下一个区块
     * @param data 区块中的数据
     * @return 新区块
     */
    private Block generateNextBlock(String data) {
        Block block = new Block();
        // 给 索引、时间戳、前一区块的哈希值、数据 这四个属性赋值
        Block previousBlock = getLastBlock();
        block.setIndex(previousBlock.getIndex() + 1);
        block.setTimestamp(System.currentTimeMillis());
        block.setPreviousHash(previousBlock.getHash());
        block.setData(data);
        // 计算 nonce 和 哈希值
        calculateNonceAndHash(block);
        return block;
    }

    /**
     * 获取当前区块链中最新的区块
     * @return
     */
    private Block getLastBlock() {
        return blockChain.get(blockChain.size() - 1);
    }

    /**
     * 向区块链中添加新的区块
     * @param block 要被添加的区块
     */
    public void addBlock(Block block) {
        // 先判断新区块的合法性，再添加
        if (isValidBlock(block, getLastBlock())) {
            blockChain.add(block);
        }
    }

    /**
     * 验证区块是否合法
     * @param block 待验证的区块
     * @param previousBlock 前一个区块
     * @return 是否合法
     */
    public boolean isValidBlock(Block block, Block previousBlock) {
        // 判断索引
        if (block.getIndex() != previousBlock.getIndex() + 1) {
            System.out.println("索引错误");
            return false;
        }
        // 判断时间戳
        if (block.getTimestamp() < previousBlock.getTimestamp()) {
            System.out.println("时间戳错误");
            return false;
        }
        // 判断前一区块哈希
        if (!block.getPreviousHash().equals(previousBlock.getHash())) {
            System.out.println("前一区块哈希错误");
            return false;
        }
        // 判断哈希值是否正确
        if (!HashUtil.getSHA256(block.getOriginal()).equals(block.getHash())) {
            System.out.println("哈希值错误");
            return false;
        }
        System.out.println("区块合法");
        return true;
    }

    /**
     * 挖矿（计算 nonce 和哈希值）
     * @param block
     */
    private void calculateNonceAndHash(Block block) {
        long nonce = 0;
        while (true) {
            System.out.println("当前的 nonce 值是：" + nonce);
            block.setNonce(nonce);
            String hash = HashUtil.getSHA256(block.getOriginal());
            if (isValidHash(hash)) {
                System.out.println("找到了合法的哈希值：" + hash);
                block.setHash(hash);
                break;
            }
            nonce++;
        }
    }

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

    @Override
    public String toString() {
        return "BlockChain{" +
                "blockChain=" + blockChain +
                '}';
    }

    public static void main(String[] args) {
        // 创建区块链：初始化，并添加创世区块
        BlockChain blockChain = new BlockChain();
        System.out.println(blockChain);
        // 生成新的区块 block1
        Block block1 = blockChain.generateNextBlock("你好");
        // 将 block1 添加到区块链中
        blockChain.addBlock(block1);
        // 生成新区块 block2
        Block block2 = blockChain.generateNextBlock("今天天气不错");
        // 手动修改信息，触发错误情况
        block2.setTimestamp(0L);
        // 将 block2 添加到区块链当中（由于前面修改数据，会造成验证不通过，无法添加）
        blockChain.addBlock(block2);
        // 打印当前区块链的所有数据
        System.out.println(blockChain);
    }

}
