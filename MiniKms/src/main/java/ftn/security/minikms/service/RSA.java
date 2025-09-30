package ftn.security.minikms.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class RSA {
    public static String createKey() throws NoSuchAlgorithmException {
        KeyPair key = generateKey();
        //save key
        return "keyId";
    }
    private static KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }
    public static void rotateKey(String Id){
        //rotate
    }
}
