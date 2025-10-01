package ftn.security.minikms.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "key_versions")
public class WrappedKey {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer version;

    @Embedded
    private KeyMaterial wrappedMaterial;

    @ManyToOne
    @JoinColumn(name = "key_metadata_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private KeyMetadata metadata;

    public static WrappedKey of(KeyMaterial wrappedMaterial, KeyMetadata metadata) {
        var entity = new WrappedKey();
        entity.version = 1;
        entity.wrappedMaterial = wrappedMaterial;
        entity.metadata = metadata;
        return entity;
    }
}
