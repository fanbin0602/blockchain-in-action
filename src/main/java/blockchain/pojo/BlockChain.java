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
     * 难度值
     */
    private static final int DIFFICULTY = 3;
    private static final char ZERO = '0';

    /**
     * 区块列表：保存区块链当中的所有区块
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
        // 设置创世区块的信息
        Block block = new Block(0,
                1578000000000L,
                "0",
                "GENESIS BLOCK");
        // 计算 nonce 和哈希值
        calculateNonceAndHash(block);
        return block;
    }

    /**
     * 生成下一个区块
     * @param data 区块中的数据
     * @return 新区块
     */
    public Block generateNextBlock(String data) {
        // 获取最新的区块
        Block previousBlock = getLastBlock();
        // 根据最新区块的信息，创建下一个区块
        Block block = new Block(previousBlock.getIndex() + 1,
                System.currentTimeMillis(),
                previousBlock.getHash(),
                data);
        // 计算下一个区块的 nonce 和 哈希值
        calculateNonceAndHash(block);
        return block;
    }

    /**
     * 获取当前区块链中最新的区块
     * @return
     */
    public Block getLastBlock() {
        // 获取区块列表中的最后一个元素
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
        if (!HashUtil.getSHA256(block.originalString()).equals(block.getHash())) {
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
        // 从 0 开始尝试 nonce 值
        long nonce = 0;
        // 一直循环，每次给 nonce 值 +1，直到计算出符合要求的哈希推出循环
        while (true) {
            // 把当前的 nonce 值赋值给区块的 nonce 属性
            block.setNonce(nonce);
            // 计算哈希值
            String hash = HashUtil.getSHA256(block.originalString());
            // 如果哈希值符合难度要求，则把符合要求的哈希值赋值给区块的哈希值，结束循环
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
        // 如果哈希值为空，则不合法
        if (hash == null) { return false; }
        // 循环判断哈希值的每个字符
        for (int i = 0; i < hash.length(); i++) {
            // 判断第一个不是0的字符是哈希值的第几位
            // 如果这个位数大于难度值，则哈希值符合难度要求
            if (hash.charAt(i) != ZERO) {
                return i >= DIFFICULTY;
            }
        }
        return true;
    }

    /**
     * 替换区块链
     * @param newBlockChain 新区块链数据
     */
    public void replaceChain(List<Block> newBlockChain) {
        // 验证条件：新的区块链必须是合法的区块链 并且 新的区块链比当前的区块链长
        if (isValidBlocks(newBlockChain) && newBlockChain.size() > blockChain.size()) {
            // 验证通过，直接把新的区块列表赋值给区块链中的 blockChain 属性
            blockChain = newBlockChain;
        }
    }

    /**
     * 判断一个区块连是否合法
     * @param blockChain 待判断的区块链数据
     * @return 判断结果
     */
    public boolean isValidBlocks(List<Block> blockChain) {
        // 新区块链和当前区块链的创世区块链必须是一样的
        Block genesisBlock = blockChain.get(0);
        if (!getGenesisBlock().equals(genesisBlock)) {
            return false;
        }
        // 新区块链里面所有的区块都是合法的
        for (int i = 1; i < blockChain.size(); i++) {
            // 如果有任意一个区块不合法，直接返回 false
            if (!isValidBlock(blockChain.get(i), blockChain.get(i - 1))){
                System.out.println("区块不合法，索引是：" + i);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取创世区块
     * @return
     */
    private Block getGenesisBlock() {
        // 直接获取区块列表中的第一个元素即可
        return blockChain.get(0);
    }

    @Override
    public String toString() {
        return "BlockChain{" +
                "blockChain=" + blockChain +
                '}';
    }

    public static void main(String[] args) {

        // 创建两个区块链
        BlockChain bc1 = new BlockChain();
        BlockChain bc2 = new BlockChain();

        System.out.println(bc1.getGenesisBlock().equals(bc2.getGenesisBlock()));

        // 向 bc1 添加区块
        Block block11 = bc1.generateNextBlock("1-1");
        bc1.addBlock(block11);
        Block block12 = bc1.generateNextBlock("1-2");
        bc1.addBlock(block12);

        // 向 bc2 添加区块
        Block block21 = bc2.generateNextBlock("2-1");
        bc2.addBlock(block21);
        Block block22 = bc2.generateNextBlock("2-2");
        bc2.addBlock(block22);
        Block block23 = bc2.generateNextBlock("2-3");
        bc2.addBlock(block23);
        // block23.setHash("lsdjflksdjflksdjflskdjflksjdfklsd");


        // 打印两个区块链内容
        System.out.println("区块链1中的数据：");
        System.out.println(bc1);
        System.out.println("区块链2中的数据：");
        System.out.println(bc2);

        System.out.println("用 bc2 的数据替换 bc1 的数据");
        bc1.replaceChain(bc2.getBlockChain());

        System.out.println("区块链1中的数据：");
        System.out.println(bc1);
        System.out.println("区块链2中的数据：");
        System.out.println(bc2);


//        // 创建区块链：初始化，并添加创世区块
//        BlockChain blockChain = new BlockChain();
//        System.out.println(blockChain);
//        // 生成新的区块 block1
//        Block block1 = blockChain.generateNextBlock("你好");
//        // 将 block1 添加到区块链中
//        blockChain.addBlock(block1);
//        // 生成新区块 block2
//        Block block2 = blockChain.generateNextBlock("今天天气不错");
//        // 手动修改信息，触发错误情况
//        block2.setTimestamp(0L);
//        // 将 block2 添加到区块链当中（由于前面修改数据，会造成验证不通过，无法添加）
//        blockChain.addBlock(block2);
//        // 打印当前区块链的所有数据
//        System.out.println(blockChain);
    }

}
