package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMaterial;
import ftn.security.minikms.repository.KeyMetadataRepository;
import ftn.security.minikms.repository.WrappedKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

@Service
@Transactional
public class KeyComputeService {
    private final KeyMetadataRepository metadataRepository;
    private final WrappedKeyRepository keyRepository;
    private final AESService aesService;
    private final RSAService rsaService;
    private final HMACService hmacService;

    @Autowired
    private RootKeyManager rootKeyManager;

    public KeyComputeService(KeyMetadataRepository metadataRepository, WrappedKeyRepository keyRepository) {
        this.metadataRepository = metadataRepository;
        this.keyRepository = keyRepository;
        this.aesService = new AESService();
        this.rsaService = new RSAService();
        this.hmacService = new HMACService();
    }

    public String encryptAes(String message, UUID keyId, Integer version)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return aesService.encrypt(message, getKey(keyId, version));
    }

    public String decryptAes(String message, UUID keyId, Integer version)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return aesService.decrypt(message, getKey(keyId, version));
    }

    public String encryptRsa(String message, UUID keyId, Integer version)
            throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException,
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        return rsaService.encrypt(message, getKey(keyId, version));
    }

    public String decryptRsa(String message, UUID keyId, Integer version)
            throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException,
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        return rsaService.decrypt(message, getKey(keyId, version));
    }

    public String computeHmac(String message, UUID keyId, Integer version) throws Exception {
        return hmacService.computeHmac(message, getKey(keyId, version));
    }

    public boolean verifyHmac(String message, String hmacBase64, UUID keyId, Integer version)
            throws Exception {
        return hmacService.verifyHmac(message,hmacBase64, getKey(keyId, version));
    }

    public KeyMaterial getKey(UUID keyId, Integer version) {
        var metadata = metadataRepository.findById(keyId)
                .orElseThrow(() -> new InvalidParameterException("Key with given id does not exist"));

        if (version == null) version = metadata.getPrimaryVersion();
        var wrappedKey = keyRepository.findByMetadataIdAndVersion(keyId, version)
                .orElseThrow(() -> new InvalidParameterException("Key with given id and version does not exist"));

        return wrappedKey.getWrappedMaterial();
    }

    public KeyMaterial getKeySig(UUID keyId, Integer version) {
        var metadata = metadataRepository.findById(keyId)
                .orElseThrow(() -> new InvalidParameterException("Key with given id does not exist"));

        if (version == null) version = metadata.getPrimaryVersion();
        var wrappedKey = keyRepository.findByMetadataIdAndVersion(keyId, version)
                .orElseThrow(() -> new InvalidParameterException("Key with given id and version does not exist"));

        var wrappedMaterial = wrappedKey.getWrappedMaterial();

        try {
            // Unwrap the private key
            byte[] unwrappedPrivate = rootKeyManager.unwrap(
                    wrappedMaterial.getKey(), keyId, version
            );

            // For asymmetric keys: also store public key
            KeyMaterial material = new KeyMaterial();
            material.setKey(unwrappedPrivate);
            material.setPublicKey(wrappedMaterial.getPublicKey());

            return material;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to unwrap key material", e);
        }
    }

}
