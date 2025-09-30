package ftn.security.minikms.service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HMAC {
    public static String createKey() throws NoSuchAlgorithmException {
        SecretKey key = generateKey();
        //save key
        return "keyId";
    }
    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA512");
        keyGenerator.init(256, SecureRandom.getInstanceStrong());
        return keyGenerator.generateKey();
    }
    public static void rotateKey(String Id){
        //rotate
    }
}
