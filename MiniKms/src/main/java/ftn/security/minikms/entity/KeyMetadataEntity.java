package ftn.security.minikms.entity;

import ftn.security.minikms.enumeration.KeyOperation;
import ftn.security.minikms.enumeration.KeyType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class KeyMetadataEntity {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String alias;
    private Integer primaryVersion;

    @Enumerated(EnumType.STRING)
    private KeyType keyType;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<KeyOperation> allowedOperations;

    private Instant createdAt;
    private Instant rotatedAt;

    @OneToMany(mappedBy = "metadata", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WrappedKeyEntity> versions;

    public static KeyMetadataEntity of(String alias, KeyType keyType, List<KeyOperation> allowedOperations) {
        var entity = new KeyMetadataEntity();
        entity.alias = alias;
        entity.primaryVersion = 0;
        entity.keyType = keyType;
        entity.allowedOperations = allowedOperations;
        entity.createdAt = Instant.now();
        return entity;
    }

    public void updatePrimaryVersion(Integer version) {
        primaryVersion = version;

        if (version > 1) {
            rotatedAt = Instant.now();
        }
    }
}
