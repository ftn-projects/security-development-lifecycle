package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMaterial;
import ftn.security.minikms.entity.KeyMetadata;
import ftn.security.minikms.entity.WrappedKey;
import ftn.security.minikms.enumeration.KeyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.UUID;

@Service
public class SignatureService {

    @Autowired
    private KeyComputeService keyComputeService;

    public SignatureService() {
    }

    public byte[] sign(UUID keyId, String message, Integer version) throws GeneralSecurityException {
        KeyMaterial keyMaterial = keyComputeService.getKeySig(keyId, version);

        if (keyMaterial.getPublicKey() == null) {
            throw new IllegalArgumentException("Key is not asymmetric and cannot be used for signing");
        }


        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyMaterial.getKey()));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        return signature.sign();
    }

    public boolean verify(UUID keyId, String message, byte[] signatureBytes, Integer version) throws GeneralSecurityException {
        KeyMaterial keyMaterial = keyComputeService.getKeySig(keyId, version);

        if (keyMaterial.getPublicKey() == null) {
            throw new IllegalArgumentException("Key is not asymmetric and cannot be used for verification");
        }

        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(keyMaterial.getPublicKey()));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        return signature.verify(signatureBytes);
    }
}
