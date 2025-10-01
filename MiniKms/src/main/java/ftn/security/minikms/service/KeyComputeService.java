package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMaterial;
import ftn.security.minikms.repository.KeyMetadataRepository;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

@Service
public class KeyComputeService {
    private final KeyMetadataRepository metadataRepository;
    private final AESService aesService;
    private final RSAService rsaService;
    private final HMACService hmacService;
    private static final String NOT_AUTHORIZED_MSG = "You do not own a key with given id";

    public KeyComputeService(KeyMetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
        this.aesService = new AESService();
        this.rsaService = new RSAService();
        this.hmacService = new HMACService();
    }
    public String encryptAes(String message, UUID keyId, String username, Integer version)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return aesService.encrypt(message, getKey(keyId, username, version));
    }
    public String decryptAes(String message, UUID keyId, String username, Integer version)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return aesService.decrypt(message, getKey(keyId, username, version));
    }
    public String encryptRsa(String message, UUID keyId, String username, Integer version)
            throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException,
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        return rsaService.encrypt(message, getKey(keyId, username, version));
    }
    public String decryptRsa(String message, UUID keyId, String username, Integer version)
            throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException,
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        return rsaService.decrypt(message, getKey(keyId, username, version));
    }
    public String computeHmac(String message, UUID keyId, String username, Integer version) throws Exception {
        return hmacService.computeHmac(message, getKey(keyId, username, version));
    }
    public boolean verifyHmac(String message, String hmacBase64, UUID keyId, String username, Integer version)
            throws Exception {
        return hmacService.verifyHmac(message,hmacBase64, getKey(keyId, username, version));
    }
    public KeyMaterial getKey(UUID keyId, String username, Integer version){
        var metadata = metadataRepository.findByIdAndUserUsername(keyId, username)
                .orElseThrow(() -> new InvalidParameterException(NOT_AUTHORIZED_MSG));
        var wrappedKey =  version != null? metadata.getVersion(version) : metadata.getVersion(metadata.getPrimaryVersion());
        return wrappedKey.getWrappedMaterial();
    }
}
