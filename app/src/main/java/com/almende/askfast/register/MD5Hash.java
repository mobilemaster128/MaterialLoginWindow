package com.almende.askfast.register;

/**
 * Created by Freeware Sys on 8/21/2016.
 */
public class MD5Hash {
    private String strInput;

    public MD5Hash(String input) {
        this.strInput = input;
    }

    public String getMD5() {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(strInput.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
