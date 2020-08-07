package me.patothebest.gamecore.tests;

import me.patothebest.gamecore.util.CryptoUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionTest {

    // 8-byte Salt
    private static final byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    /**
     *
     * @param secretKey Key used to encrypt data
     * @param plainText Text input to be encrypted
     * @return Returns encrypted text
     *
     */
    public static String encrypt(String secretKey, String plainText)  throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        //Key generation for enc and desc
        int iterationCount = 19;
        KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        // Prepare the parameter to the ciphers
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

        //Enc process
        Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
        ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        String charSet="UTF-8";
        byte[] in = plainText.getBytes(charSet);
        byte[] out = ecipher.doFinal(in);
        return Base64.getEncoder().encodeToString(out);
    }

    public static void main(String... args) {
        CryptoUtil cryptoUtil=new CryptoUtil();
        String key= "TheTowers";
        String plain="1644";
        String enc= null;
        try {
            enc = encrypt(key, plain);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Original text: "+plain);
        System.out.println("Encrypted text: "+enc);
        String plainAfter= null;

        try {
            plainAfter = cryptoUtil.decrypt(key, enc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Original text after decryption: "+plainAfter);
    }

    final CryptoUtil cryptoUtil = new CryptoUtil();
    final String key = "TheTowers";
    final String plain= "TheTowersReloaded";
    String enc;
    String plainAfter;

    @Test
    public void encrypt() throws Exception {
        enc = encrypt(key, plain);
        Assert.assertTrue(enc.equals("IF/OdzFU85bDqgDEzvfhCPeXt6jU07+E"));
    }

    @Test
    public void decrypt() throws Exception {
        plainAfter = cryptoUtil.decrypt(key, "IF/OdzFU85bDqgDEzvfhCPeXt6jU07+E");
        Assert.assertTrue(plainAfter.equals(plain));
    }

    @Test(expected = Exception.class)
    public void decryptNotKey() throws Exception {
        plainAfter = cryptoUtil.decrypt("aaaaaaa", "IF/OdzFU85bDqgDEzvfhCPeXt6jU07+E");
    }

    @Test(expected = Exception.class)
    public void decryptNotText() throws Exception {
        plainAfter = cryptoUtil.decrypt(key, "ASDHASOD");
    }
}