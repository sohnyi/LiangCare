package com.sohnyi.liangcare.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sohnyi on 2017/4/25.
 */

public class Encoder {
    public static String md5Encode(String string) throws Exception {
        byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static String sha256Encode(String str) throws UnsupportedEncodingException {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
            mDigest.reset();
            byte[] hash = mDigest.digest(str.getBytes("UTF-8"));
            String val = String.format("%0" + (hash.length * 2 + "X"),
                    new BigInteger(1, hash));
            return val;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
