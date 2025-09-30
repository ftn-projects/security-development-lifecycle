package ftn.security.minikms.service;

import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;

@Service
public class KeyService {
    public String createKey(String keyType) throws NoSuchAlgorithmException, InvalidParameterException {
        return switch (keyType) {
            case "symmetric" -> AES.createKey();
            case "asymmetric" -> RSA.createKey();
            case "hmac" -> HMAC.createKey();
            default -> throw new InvalidParameterException();
        };
    }
    public void deleteKey(String Id){
        //delete from database
    }
    public void rotateKey(String keyType, String keyId) throws InvalidParameterException {
        switch(keyType){
            case "symmetric" -> AES.rotateKey(keyId);
            case "asymmetric" -> RSA.rotateKey(keyId);
            case "hmac" -> HMAC.rotateKey(keyId);
            default -> throw new InvalidParameterException();
        }
    }
}
