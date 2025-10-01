package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMaterial;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

class HMACService implements ICryptoService {
    public KeyMaterial generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA512");
        keyGenerator.init(256, SecureRandom.getInstanceStrong());
        var key = keyGenerator.generateKey();
        return KeyMaterial.of(key);
    }
}
