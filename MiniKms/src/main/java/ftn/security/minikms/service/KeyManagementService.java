package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMetadata;
import ftn.security.minikms.entity.User;
import ftn.security.minikms.entity.WrappedKey;
import ftn.security.minikms.enumeration.KeyOperation;
import ftn.security.minikms.enumeration.KeyType;
import ftn.security.minikms.repository.KeyMetadataRepository;
import ftn.security.minikms.repository.UserRepository;
import ftn.security.minikms.repository.WrappedKeyRepository;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class KeyManagementService {
    private final KeyMetadataRepository metadataRepository;
    private final WrappedKeyRepository keyRepository;
    private final UserRepository userRepository;
    private final RootKeyManager rootKeyManager;
    private final Map<KeyType, ICryptoService> cryptoServices;
    private static final String KEY_NOT_FOUND = "Key with given id does not exist";

    public KeyManagementService(
            KeyMetadataRepository metadataRepository,
            WrappedKeyRepository keyRepository,
            UserRepository userRepository,
            RootKeyManager rootKeyManager) {
        this.metadataRepository = metadataRepository;
        this.keyRepository = keyRepository;
        this.userRepository = userRepository;
        this.rootKeyManager = rootKeyManager;
        this.cryptoServices = Map.of(
                KeyType.SYMMETRIC, new AESService(),
                KeyType.ASYMMETRIC, new RSAService(),
                KeyType.HMAC, new HMACService()
        );
    }

    public KeyMetadata createKey(String alias, KeyType keyType, List<KeyOperation> allowedOperations, String username)
            throws InvalidParameterException, GeneralSecurityException {
        var user = findUserByUsername(username);
        var metadata = metadataRepository.save(KeyMetadata.of(alias, keyType, allowedOperations, user));
        return createNewKeyVersion(metadata, 1);
    }

    public void deleteKey(UUID id) throws InvalidParameterException {
        metadataRepository.deleteById(id);
    }

    public KeyMetadata rotateKey(UUID id) throws InvalidParameterException, GeneralSecurityException {
        var metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new InvalidParameterException(KEY_NOT_FOUND));

        var nextVersion = metadata.getPrimaryVersion() + 1;
        return createNewKeyVersion(metadata, nextVersion);
    }

    private KeyMetadata createNewKeyVersion(KeyMetadata metadata, Integer version) throws GeneralSecurityException {
        var id = metadata.getId();
        var keyType = metadata.getKeyType();
        metadata.updatePrimaryVersion(version); // Set the latest version as primary
        var saved = metadataRepository.save(metadata);

        var material = cryptoServices.get(keyType).generateKey();
        var secretKey = material.getKey();

        // Not wrapping public key, just secret
        var wrapped = rootKeyManager.wrap(secretKey, id, version);
        if (secretKey != null) java.util.Arrays.fill(secretKey, (byte) 0); // Zeroizing sensitive in memory data

        material.setKey(wrapped);

        keyRepository.save(WrappedKey.of(version, material, saved));
        return saved;
    }

    private User findUserByUsername(String username) throws InvalidParameterException {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new InvalidParameterException("User with given username does not exist"));
    }

    public List<KeyMetadata> getAllKeys() {
        return metadataRepository.findAll();
    }

    public KeyMetadata getKeyById(UUID id) {
        return metadataRepository.findById(id).orElseThrow(() ->
                new InvalidParameterException("Key with given id does not exist"));
    }
}
