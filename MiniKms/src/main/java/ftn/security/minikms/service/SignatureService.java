package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMaterial;
import ftn.security.minikms.entity.KeyMetadata;
import ftn.security.minikms.entity.WrappedKey;
import ftn.security.minikms.enumeration.KeyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.UUID;

@Service
public class SignatureService {

    @Autowired
    private KeyComputeService keyComputeService;

    public SignatureService() {
    }

    public byte[] sign(UUID keyId, String message) throws GeneralSecurityException {
        KeyMaterial keyMaterial = keyComputeService.getKey(keyId, null);

        // Ensure it's asymmetric
        if (keyMaterial.getPublicKey() == null) {
            throw new IllegalArgumentException("Key is not asymmetric and cannot be used for signing");
        }

        System.out.println("Private key bytes length = " + keyMaterial.getKey().length);
        System.out.println("Public key bytes length = " + keyMaterial.getPublicKey().length);

        // Reconstruct private key from PKCS#8
        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyMaterial.getKey()));

        // Sign message
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        return signature.sign();
    }
}
