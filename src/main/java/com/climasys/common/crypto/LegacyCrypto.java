package com.climasys.common.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.spec.KeySpec;
import java.util.Base64;

public class LegacyCrypto {

    private static final byte[] SALT = new byte[] {
            (byte)0x49, (byte)0x76, (byte)0x61, (byte)0x6e, (byte)0x20, (byte)0x4d, (byte)0x65, (byte)0x64,
            (byte)0x76, (byte)0x65, (byte)0x64, (byte)0x65, (byte)0x76
    };

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static String encryptUnicode(String key, String clearText) {
        try {
            byte[] clearBytes = toUtf16Le(clearText);
            KeySpec spec = new PBEKeySpec(key.toCharArray(), SALT, 1000, 384); // 32 bytes key + 16 bytes IV = 384 bits
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyIv = factory.generateSecret(spec).getEncoded();
            byte[] keyBytes = new byte[32];
            byte[] ivBytes = new byte[16];
            System.arraycopy(keyIv, 0, keyBytes, 0, 32);
            System.arraycopy(keyIv, 32, ivBytes, 0, 16);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(ivBytes));
            byte[] encrypted = cipher.doFinal(clearBytes);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    public static String decryptUnicode(String key, String cipherTextBase64) {
        try {
            byte[] cipherBytes = Base64.getDecoder().decode(cipherTextBase64);
            KeySpec spec = new PBEKeySpec(key.toCharArray(), SALT, 1000, 384);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyIv = factory.generateSecret(spec).getEncoded();
            byte[] keyBytes = new byte[32];
            byte[] ivBytes = new byte[16];
            System.arraycopy(keyIv, 0, keyBytes, 0, 32);
            System.arraycopy(keyIv, 32, ivBytes, 0, 16);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(ivBytes));
            byte[] decrypted = cipher.doFinal(cipherBytes);
            return fromUtf16Le(decrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }

    private static byte[] toUtf16Le(String s) {
        return s.getBytes(Charset.forName("UTF-16LE"));
    }

    private static String fromUtf16Le(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-16LE"));
    }
}


