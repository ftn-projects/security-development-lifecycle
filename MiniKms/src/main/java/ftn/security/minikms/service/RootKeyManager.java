package ftn.security.minikms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.UUID;

@Service
public class RootKeyManager {
    private static final java.security.SecureRandom RNG = new java.security.SecureRandom();
    private final SecretKey rootKey;

    public RootKeyManager(@Value("${ROOT_KEY}") String base64Key) {
        if (base64Key == null || base64Key.isEmpty()) {
            throw new IllegalArgumentException("ROOT_KEY environment variable is not set or empty");
        }

        byte[] raw = Base64.getDecoder().decode(base64Key);
        this.rootKey = new SecretKeySpec(raw, "AES");
    }

    public byte[] wrap(byte[] plaintextKey, UUID id, Integer version) throws GeneralSecurityException {
        var iv = new byte[12];
        RNG.nextBytes(iv);

        var aad = getAad(id, version);
        var c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, rootKey, new GCMParameterSpec(128, iv));
        if (aad.length > 0) c.updateAAD(aad);
        var ct = c.doFinal(plaintextKey);

        var out = new byte[iv.length + ct.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(ct, 0, out, iv.length, ct.length);
        return out;
    }

    public byte[] unwrap(byte[] blob, UUID id, Integer version) throws GeneralSecurityException {
        if (blob.length < 12 + 16) throw new GeneralSecurityException("Blob too short");
        var aad = getAad(id, version);
        var iv = java.util.Arrays.copyOfRange(blob, 0, 12);
        var ct = java.util.Arrays.copyOfRange(blob, 12, blob.length);

        var c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.DECRYPT_MODE, rootKey, new GCMParameterSpec(128, iv));
        if (aad.length > 0) c.updateAAD(aad);
        return c.doFinal(ct);
    }

    private static byte[] getAad(UUID id, Integer version) {
        if (id == null || version == null) {
            throw new IllegalArgumentException("id and version must not be null");
        }
        var aadString = id + ":" + version;
        return aadString.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
