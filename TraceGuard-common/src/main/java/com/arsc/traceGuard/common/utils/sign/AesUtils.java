package com.arsc.traceGuard.common.utils.sign;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * AES 加密工具类 (ECB模式/PKCS5Padding)
 * 输出转为 Hex 字符串，确保 URL 安全
 */
public class AesUtils {

    // 默认密钥 (正式上线建议放在配置文件或环境变量中)
    // AES-128 需要 16 位字符串
    private static final String DEFAULT_KEY = "TraceGuard@2026!";

    /**
     * 加密
     * @param content 明文 (UUID)
     * @return 密文 (Hex字符串)
     */
    public static String encrypt(String content) {
        try {
            SecretKeySpec key = new SecretKeySpec(DEFAULT_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return parseByte2HexStr(encrypted); // 转 Hex
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     * @param content 密文 (Hex字符串)
     * @return 明文 (UUID)
     */
    public static String decrypt(String content) {
        try {
            byte[] contentBytes = parseHexStr2Byte(content);
            SecretKeySpec key = new SecretKeySpec(DEFAULT_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(contentBytes);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 解密失败通常意味着伪造码
        }
    }

    // 二进制转十六进制字符串
    private static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    // 十六进制字符串转二进制
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}