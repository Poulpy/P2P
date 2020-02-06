package fr.uvsq.fsp.util;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 * Credit goes to :
 * https://www.quickprogrammingtips.com/java/how-to-create-checksum-value-in-java.html
 */
public class Checksum {

    /**
     * Returns a hash of a string. Accepts MD5, SHA-256.
     */
    public static String getHash(String hashType, String data) {
        String result = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(hashType);
            byte[] hash = messageDigest.digest(data.getBytes("UTF-8"));
            result = bytesToHex(hash);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return result;
    }

    /**
     * Returns a MD5 hash
     */
    public static String getMD5Hash(String data) {
        return getHash("MD5", data);
    }

    /**
     * Returns a SHA-256 hash
     */
    public static String getSHA265Hash(String data) {
        return getHash("SHA-256", data);
    }

    /**
     * Converts bytes to hexadecimal
     */
    public static String bytesToHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }
}

