package org.wikiup.modules.crypt.util;

import org.wikiup.core.Wikiup;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ValueUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class Crypt {
    private static KeyGenerator GENERATOR = null;

    static public byte[] stringToByteArray(String s) {
        byte b[] = new byte[s.length() / 2];
        int i;
        for(i = 0; i < b.length; i++)
            b[i] = (byte) (ValueUtil.toHex(s.charAt(i * 2)) * 16 + ValueUtil.toHex(s.charAt(i * 2 + 1)));
        return b;
    }

    static public String MD5Encrypt(String origin) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = ValueUtil.toHexString(md.digest(origin.getBytes()));
        } catch(NoSuchAlgorithmException ex) {
            Assert.fail(ex);
        }
        return resultString;
    }

    private static Key getKey(String k) throws NoSuchAlgorithmException {
        if(GENERATOR == null)
            GENERATOR = KeyGenerator.getInstance("DES");
        GENERATOR.init(new SecureRandom(k.getBytes()));
        return GENERATOR.generateKey();
    }

    public static String DESEncrypt(String s, String k) {
        try {
            byte b[] = s.getBytes();
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, getKey(k));
            return ValueUtil.toHexString(cipher.doFinal(b, 0, b.length));
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public static String DESDecrypt(String s, String k) {
        try {
            byte b[] = stringToByteArray(s);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, getKey(k));
            return new String(cipher.doFinal(b, 0, b.length));
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public static byte[] RSAEncrypt(byte[] data, Key key) {
        Cipher cipher = getCipherInstance("RSA", key, Cipher.ENCRYPT_MODE);
        int inputBlockSize = cipher.getBlockSize();
        int outputBlockSize = cipher.getOutputSize(data.length);
        int blockCount = (data.length - 1) / inputBlockSize + 1;
        byte raw[] = new byte[outputBlockSize * blockCount];
        int i;
        try {
            for(i = 0; i * inputBlockSize < data.length; i++)
                cipher.doFinal(data, i * inputBlockSize,
                        Math.min(inputBlockSize,
                                data.length - i * inputBlockSize), raw,
                        i * outputBlockSize);
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return raw;
    }

    public static byte[] RSADecrypt(byte[] raw, Key key) {
        Cipher cipher = getCipherInstance("RSA", key, Cipher.DECRYPT_MODE);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int blockSize = cipher.getBlockSize();
        int i;
        try {
            for(i = 0; i < raw.length; i += blockSize)
                bout.write(cipher.doFinal(raw, i, blockSize));
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return bout.toByteArray();
    }

    private static Cipher getCipherInstance(String name, Key key, int mode) {
        return getCipherInstance(name, key, mode, new SecureRandom());
    }

    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA", getCipherProvider());
            keyPairGen.initialize(1024, new SecureRandom());
            return keyPairGen.genKeyPair();
        } catch(NoSuchAlgorithmException ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public static RSAPublicKey getRSAPublicKey(byte[] modulus, byte[] publicExponent) {
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA", getCipherProvider());
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(modulus),
                    new BigInteger(publicExponent));
            return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public static RSAPrivateKey getRSAPrivateKey(byte[] modulus, byte[] privateExponent) {
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA", getCipherProvider());
            RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(new BigInteger(
                    modulus), new BigInteger(privateExponent));
            return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return null;
    }

    private static Cipher getCipherInstance(String name, Key key, int mode, SecureRandom random) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(name, getCipherProvider());
            cipher.init(mode, key, random);
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return cipher;
    }

    private static Provider getCipherProvider() {
        return Wikiup.getInstance().get(Provider.class, "crypt-provider:default");
    }

}
