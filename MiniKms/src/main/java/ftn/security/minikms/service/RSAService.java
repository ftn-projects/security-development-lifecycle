package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMaterial;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

class RSAService implements ICryptoService {
    public KeyMaterial generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var pair = generator.generateKeyPair();
        return KeyMaterial.of(pair);
    }
}
