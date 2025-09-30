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
public class WrappedKeyEntity {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer version;

    @Embedded
    private KeyMaterial wrappedMaterial;

    @ManyToOne
    @JoinColumn(name = "key_metadata_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private KeyMetadataEntity metadata;

    public static WrappedKeyEntity of(KeyMaterial wrappedMaterial, KeyMetadataEntity metadata) {
        var entity = new WrappedKeyEntity();
        entity.version = 1;
        entity.wrappedMaterial = wrappedMaterial;
        entity.metadata = metadata;
        return entity;
    }
}
