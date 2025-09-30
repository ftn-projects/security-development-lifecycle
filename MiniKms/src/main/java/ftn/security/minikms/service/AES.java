package ftn.security.minikms.service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class AES {
    public static String createKey() throws NoSuchAlgorithmException {
        SecretKey key = generateKey();
        //save key
        return "keyId";
    }
    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }
    public static void rotateKey(String Id){
        //rotate
    }
}
