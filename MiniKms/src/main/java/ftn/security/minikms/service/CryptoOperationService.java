package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMaterial;
import ftn.security.minikms.entity.KeyMetadata;
import ftn.security.minikms.entity.WrappedKey;
import ftn.security.minikms.enumeration.KeyType;
import ftn.security.minikms.repository.KeyMetadataRepository;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@Service
public class CryptoOperationService {

    private final KeyManagementService keyService;
    private final RootKeyManager rootKeyManager;
    private final KeyMetadataRepository keyMetadataRepository;

    public CryptoOperationService(KeyManagementService keyService, RootKeyManager rootKeyManager, KeyMetadataRepository keyMetadataRepository) {
        this.keyService = keyService;
        this.rootKeyManager = rootKeyManager;
        this.keyMetadataRepository = keyMetadataRepository;
    }

    public byte[] sign(UUID keyId, byte[] data, String username) throws GeneralSecurityException {
        KeyMetadata metadata = keyService.findByIdAndUsername(keyId, username);

        if (metadata.getKeyType() != KeyType.ASYMMETRIC) {
            throw new IllegalArgumentException("Only ASYMMETRIC keys can be used for signing");
        }

        KeyMaterial keyMaterial = unwrapLatestKey(metadata);
        var privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyMaterial.getKey()));

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);

        byte[] signature = sig.sign();
        return signature;
    }

    public byte[] generateMac(UUID keyId, byte[] message, String username) throws GeneralSecurityException {
        KeyMetadata metadata = keyService.findByIdAndUsername(keyId, username);

        if (metadata.getKeyType() != KeyType.HMAC) {
            throw new IllegalArgumentException("Only HMAC keys can generate MACs");
        }

        KeyMaterial keyMaterial = unwrapLatestKey(metadata);
        SecretKey key = new SecretKeySpec(keyMaterial.getKey(), "HmacSHA512");

        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(key);
        byte[] macBytes = mac.doFinal(message);

        return macBytes;
    }

    public boolean verifyMac(UUID keyId, byte[] message, byte[] macToVerify, String username) throws GeneralSecurityException {
        KeyMetadata metadata = keyService.findByIdAndUsername(keyId, username);

        if (metadata.getKeyType() != KeyType.HMAC) {
            throw new IllegalArgumentException("Only HMAC keys can verify MACs");
        }

        KeyMaterial keyMaterial = unwrapLatestKey(metadata);
        SecretKey key = new SecretKeySpec(keyMaterial.getKey(), "HmacSHA512");

        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(key);
        byte[] expectedMac = mac.doFinal(message);

        boolean valid = java.security.MessageDigest.isEqual(expectedMac, macToVerify);
        return valid;
    }

    private KeyMaterial unwrapLatestKey(KeyMetadata metadata) throws GeneralSecurityException {
        var latestVersion = metadata.getVersions()
                .stream()
                .max((v1, v2) -> v1.getVersion().compareTo(v2.getVersion()))
                .orElseThrow(() -> new GeneralSecurityException("No key version found"));

        byte[] unwrapped = rootKeyManager.unwrap(
                latestVersion.getWrappedMaterial().getKey(),
                metadata.getId(),
                latestVersion.getVersion()
        );

        var material = new KeyMaterial();
        material.setKey(unwrapped);
        material.setPublicKey(latestVersion.getWrappedMaterial().getPublicKey());
        return material;
    }

    public boolean verifySignature(UUID keyId, byte[] message, byte[] signatureBytes) throws GeneralSecurityException {
        KeyMetadata metadata = keyMetadataRepository.findById(keyId)
                .orElseThrow(() -> new IllegalArgumentException("Key not found"));

        WrappedKey latestVersion = metadata.getVersions().stream()
                .max((v1, v2) -> v1.getVersion().compareTo(v2.getVersion()))
                .orElseThrow(() -> new GeneralSecurityException("No key version found"));

        KeyMaterial material = latestVersion.getWrappedMaterial();
        byte[] publicKeyBytes = material.getPublicKey();

        if (publicKeyBytes == null || publicKeyBytes.length == 0) {
            throw new GeneralSecurityException("Public key not available for this key");
        }

        PublicKey publicKey = java.security.KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(message);
        return sig.verify(signatureBytes);
    }

}
