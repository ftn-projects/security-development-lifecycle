package ftn.security.minikms.entity;

import ftn.security.minikms.enumeration.KeyOperation;
import ftn.security.minikms.enumeration.KeyType;
import ftn.security.minikms.logging.EntityLogger;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "keys")
@EntityListeners(EntityLogger.class)
public class KeyMetadata {
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
    @CollectionTable(name = "key_allowed_operations")
    private List<KeyOperation> allowedOperations;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User createdBy;

    private Instant createdAt;
    private Instant rotatedAt;

    @OneToMany(mappedBy = "metadata", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WrappedKey> versions;

    public static KeyMetadata of(String alias, KeyType keyType, List<KeyOperation> allowedOperations, User user) {
        var entity = new KeyMetadata();
        entity.alias = alias;
        entity.primaryVersion = 0;
        entity.keyType = keyType;
        entity.allowedOperations = allowedOperations;
        entity.createdBy = user;
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
