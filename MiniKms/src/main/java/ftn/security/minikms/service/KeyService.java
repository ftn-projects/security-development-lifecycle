package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMetadataEntity;
import ftn.security.minikms.entity.WrappedKeyEntity;
import ftn.security.minikms.enumeration.KeyOperation;
import ftn.security.minikms.enumeration.KeyType;
import ftn.security.minikms.repository.KeyMetadataRepository;
import ftn.security.minikms.repository.WrappedKeyRepository;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class KeyService {
    private final KeyMetadataRepository metadataRepository;
    private final WrappedKeyRepository keyRepository;
    private final RootKeyManager rootKeyManager;
    private final Map<KeyType, ICryptoService> cryptoServices;

    public KeyService(
            KeyMetadataRepository metadataRepository,
            WrappedKeyRepository keyRepository,
            RootKeyManager rootKeyManager) {
        this.metadataRepository = metadataRepository;
        this.keyRepository = keyRepository;
        this.rootKeyManager = rootKeyManager;
        this.cryptoServices = Map.of(
                KeyType.SYMMETRIC, new AESService(),
                KeyType.ASYMMETRIC, new RSAService(),
                KeyType.HMAC, new HMACService()
        );
    }

    public KeyMetadataEntity createKey(String alias, KeyType keyType, List<KeyOperation> allowedOperations)
            throws InvalidParameterException, GeneralSecurityException {
        var metadata = metadataRepository.save(KeyMetadataEntity.of(alias, keyType, allowedOperations));
        return createNewKeyVersion(metadata, 1);
    }

    public void deleteKey(UUID id) throws InvalidParameterException {
        if (!metadataRepository.existsById(id))
            throw new InvalidParameterException("Key with given id does not exist");

        metadataRepository.deleteById(id);
    }

    public KeyMetadataEntity rotateKey(UUID id) throws InvalidParameterException, GeneralSecurityException {
        var metadata = findMetadataById(id);
        var nextVersion = metadata.getPrimaryVersion() + 1;
        return createNewKeyVersion(metadata, nextVersion);
    }

    private KeyMetadataEntity createNewKeyVersion(KeyMetadataEntity metadata, Integer version) throws GeneralSecurityException {
        var id = metadata.getId();
        var keyType = metadata.getKeyType();

        var material = cryptoServices.get(keyType).generateKey();
        var secretKey = material.getKey();

        // Not wrapping public key, just secret
        var wrapped = rootKeyManager.wrap(secretKey, id, version);
        if (secretKey != null) java.util.Arrays.fill(secretKey, (byte) 0); // Zeroizing sensitive in memory data

        material.setKey(wrapped);

        var key = keyRepository.save(WrappedKeyEntity.of(material, metadata));
        metadata.updatePrimaryVersion(key.getVersion()); // Set the latest version as primary
        return metadataRepository.save(metadata);
    }

    private KeyMetadataEntity findMetadataById(UUID id) throws InvalidParameterException {
        return metadataRepository.findById(id).orElseThrow(() ->
                new InvalidParameterException("Key with given id does not exist"));
    }
}
