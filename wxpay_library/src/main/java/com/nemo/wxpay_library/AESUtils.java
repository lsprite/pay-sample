package com.nemo.wxpay_library;

import android.text.TextUtils;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class AESUtils {

    // 解密
    public static String DecryptDoNet(String message, String key) throws Exception {
        // String lowKey = "cl*25==1";
        String lowKey = "zk*$35=1";
        if (TextUtils.isEmpty(message)) {
            return "";
        }
        lowKey = new String(Base64.encode(lowKey.getBytes("UTF-8"), Base64.DEFAULT));
        String message_lowKey = new String(Base64.decode(message.getBytes("UTF-8"), Base64.DEFAULT));
        String message_base64 = message_lowKey.substring(0, message_lowKey.length() - lowKey.length() + 1);
        return new String(Base64.decode(message_base64.getBytes("UTF-8"), Base64.DEFAULT));
    }

    public static String EncryptAsDoNet(String message) {
        try {
            String lowKey = "zk*$35=1";
            if (TextUtils.isEmpty(message)) {
                return "";
            }
            String base64s = new String(Base64.encode(message.getBytes("UTF-8"), Base64.DEFAULT));
            String newS = base64s + new String(Base64.encode(lowKey.getBytes("UTF-8"), Base64.DEFAULT));
            String finals = new String(Base64.encode(newS.getBytes("UTF-8"), Base64.DEFAULT));
            return finals;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // 登录用加密
    public static String EncryptAsLogin(String message, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptbyte = cipher.doFinal(message.getBytes());
        return new String(Base64.encode(encryptbyte, Base64.DEFAULT));
    }

}