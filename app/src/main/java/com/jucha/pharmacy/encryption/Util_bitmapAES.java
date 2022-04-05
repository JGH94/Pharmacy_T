package com.jucha.pharmacy.encryption;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Util_bitmapAES {
    /**
     * 키가되는 문자열
     */
    private final static String KeyString = "EndofKing";
    public Util_bitmapAES() {
        // TODO Auto-generated constructor stub
    }
    /**
     * 암호화 하기
     * @param clear
     * @return
     * @throws Exception
     */
    public static byte[] encrypt( byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(getKey(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }
    /**
     * 암호화 해제
     * @param encrypted
     * @return
     * @throws Exception
     */
    public static byte[] decrypt( byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(getKey(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    private static byte[] getKey (){

        try {
            byte[] keyStart = KeyString.getBytes();
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr;
            sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(keyStart);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            return skey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }


    }
}
