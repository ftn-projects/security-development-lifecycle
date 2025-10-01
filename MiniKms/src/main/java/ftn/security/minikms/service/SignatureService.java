package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMetadata;
import ftn.security.minikms.entity.WrappedKey;
import ftn.security.minikms.enumeration.KeyType;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.UUID;

@Service
public class SignatureService {

    private final KeyManagementService keyManagementService;
    private final RootKeyManager rootKeyManager;

    public SignatureService(KeyManagementService keyManagementService, RootKeyManager rootKeyManager) {
        this.keyManagementService = keyManagementService;
        this.rootKeyManager = rootKeyManager;
    }

    public byte[] sign(UUID keyId, byte[] data, String username) throws GeneralSecurityException {
        // Get the metadata for the key and check ownership
        KeyMetadata metadata = keyManagementService.findByIdAndUsername(keyId, username);
        System.out.println("Metadata: " + metadata);

        if (metadata.getKeyType() != KeyType.ASYMMETRIC) {
            throw new IllegalArgumentException("Only ASYMMETRIC keys can be used for signing");
        }

        // Get the latest version
        WrappedKey wrappedKey = metadata.getVersion(metadata.getPrimaryVersion());
        if (wrappedKey == null) throw new GeneralSecurityException("No key version found");

        // Unwrap the private key
        byte[] unwrappedKey = rootKeyManager.unwrap(
                wrappedKey.getWrappedMaterial().getKey(),
                metadata.getId(),
                wrappedKey.getVersion()
        );

        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(unwrappedKey));

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);

        return sig.sign();
    }
}

