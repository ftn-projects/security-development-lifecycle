package ftn.security.minikms.entity;

import ftn.security.minikms.enumeration.KeyOperation;
import ftn.security.minikms.enumeration.KeyType;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class WrappedKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private UUID logicalKey;

    private String alias;
    private Integer version;

    @Enumerated(EnumType.STRING)
    private KeyType keyType;

    @Lob
    private byte[] wrappedKey;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<KeyOperation> allowedOperations;

    private Instant createdAt;

    public static WrappedKeyEntity of(UUID logicalKey, String alias, Integer version, KeyType keyType, byte[] wrappedKey, List<KeyOperation> allowedOperations) {
        var entity = new WrappedKeyEntity();
        entity.logicalKey = logicalKey;
        entity.alias = alias;
        entity.version = version;
        entity.keyType = keyType;
        entity.wrappedKey = wrappedKey;
        entity.allowedOperations = allowedOperations;
        entity.createdAt = Instant.now();
        return entity;
    }
}