package ftn.security.minikms.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

import javax.crypto.SecretKey;
import java.security.KeyPair;

@Data
@Embeddable
public class KeyMaterial {
    @Lob
    private byte[] key;
    @Lob
    private byte[] publicKey;

    public static KeyMaterial of(SecretKey key) {
        var material = new KeyMaterial();
        material.setKey(key.getEncoded());
        return material;
    }

    public static KeyMaterial of(KeyPair pair) {
        var material = new KeyMaterial();
        material.setKey(pair.getPrivate().getEncoded());
        material.setPublicKey(pair.getPublic().getEncoded());
        return material;
    }
}
