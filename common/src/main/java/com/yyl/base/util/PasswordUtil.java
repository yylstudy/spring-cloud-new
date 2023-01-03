package com.yyl.base.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;

/**
 * @author yang.yonglian
 * @version 1.0.0
 * @Description 密码工具类
 * @createTime 2022/11/15 16:12
 */
@Slf4j
public class PasswordUtil {
    /**
     * 定义使用的算法为:PBEWITHMD5andDES算法
     * 加密算法
     */
    public static final String ALGORITHM = "PBEWithMD5AndDES";
    /**
     * 定义迭代次数为1000次
     */
    private static final int ITERATIONCOUNT = 1000;

    /**
     * 密码加密
     * @param plaintext
     * @param password
     * @param salt 盐
     * @return
     */
    public static String encrypt(String plaintext, String password, String salt) {

        Key key = getPbeKey(password);
        byte[] encipheredData = null;
        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt.getBytes(), ITERATIONCOUNT);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
            encipheredData = cipher.doFinal(plaintext.getBytes("utf-8"));
        } catch (Exception e) {
            log.error("encrypt error",e);
        }
        return bytesToHexString(encipheredData);
    }

    /**
     * 根据PBE密码生成一把密钥
     * @param password
     * @return
     */
    private static Key getPbeKey(String password) {
        // 实例化使用的算法
        SecretKeyFactory keyFactory;
        SecretKey secretKey = null;
        try {
            keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            // 设置PBE密钥参数
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
            // 生成密钥
            secretKey = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return secretKey;
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param src 字节数组
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
