package blockchain.main;

import blockchain.pojo.Block;
import blockchain.pojo.BlockChain;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @author fanbin
 * @date 2020/1/3
 */
public class Main {
    public static void main(String[] args) {
        BlockChain bc = new BlockChain();
        String data = JSON.toJSONString(bc.getBlockChain().get(0));
        System.out.println(data);

        Block block = null;
        try {
            block = JSON.parseObject(data, Block.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println(block);

    }
}
