package blockchain.util;

import java.security.MessageDigest;

/**
 * 计算哈希值的工具类
 * @author fanbin
 * @date 2020/1/3
 */
public class HashUtil {

    /**
     * 计算给定字符串的 SHA-256 哈希值
     * @param str 原像
     * @return 计算结果
     */
    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception ex) {
            System.out.println("出现异常了");
        }
        return encodeStr;
    }
    /**
     * 把 byte 数组，转换为十六进制数字的字符串
     * @param bytes byte 数组
     * @return 十六进制数字字符串
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        String temp;
        for(int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                builder.append("0");
            }
            builder.append(temp);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(getSHA256("abcdefg"));
        System.out.println(getSHA256("abcdefh"));
    }

}
