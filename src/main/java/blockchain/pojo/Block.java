package blockchain.pojo;

import com.google.common.base.Objects;

/**
 * 区块
 * @author fanbin
 * @date 2020/1/3
 */
public class Block {

    /**
     * 索引
     */
    private int index;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 哈希值
     */
    private String hash;

    /**
     * 前一个区块的哈希值
     */
    private String previousHash;

    /**
     * 数据
     */
    private String data;

    /**
     * Nonce
     */
    private long nonce;

    public Block() {

    }

    public Block(int index,
                 long timestamp,
                 String hash,
                 String previousHash,
                 String data,
                 long nonce) {
        this.index = index;
        this.timestamp = timestamp;
        this.hash = hash;
        this.previousHash = previousHash;
        this.data = data;
        this.nonce = nonce;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    /**
     * 获取计算哈希值的原像字符串
     * @return
     */
    public String getOriginal() {
        return index + previousHash + timestamp + data + nonce;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", timestamp=" + timestamp +
                ", hash='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", data='" + data + '\'' +
                ", nonce=" + nonce +
                '}';
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        // 如果两个指向同一个对象，则相等
        if (this == o) { return true; }
        if (o == null) { return false; }
        if (o.getClass() != this.getClass()) {return false; }
        Block block = (Block) o;
        return this.index == block.index &&
                this.timestamp == block.timestamp &&
                this.nonce == block.nonce &&
                this.hash.equals(block.hash) &&
                this.previousHash.equals(block.previousHash) &&
                this.data.equals(block.data);
    }
}
