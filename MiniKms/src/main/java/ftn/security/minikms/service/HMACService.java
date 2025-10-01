package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMaterial;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class HMACService implements ICryptoService {
    public KeyMaterial generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA512");
        keyGenerator.init(256, SecureRandom.getInstanceStrong());
        var key = keyGenerator.generateKey();
        return KeyMaterial.of(key);
    }
    public String computeHmac(String message, KeyMaterial key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(new SecretKeySpec(key.getKey(),"HmacSHA512"));
        byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }
    public boolean verifyHmac(String message, String hmacBase64, KeyMaterial key) throws Exception {
        String computedHmac = computeHmac(message, key);
        return MessageDigest.isEqual(computedHmac.getBytes(StandardCharsets.UTF_8),
                hmacBase64.getBytes(StandardCharsets.UTF_8));
    }
}
