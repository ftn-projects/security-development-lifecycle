package ftn.security.minikms.dto;

import ftn.security.minikms.enumeration.KeyOperation;
import ftn.security.minikms.enumeration.KeyType;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class KeyMetadataDTO {
    private UUID id;
    private String alias;
    private Integer primaryVersion;
    private KeyType keyType;
    private List<KeyOperation> allowedOperations;
    private Instant createdAt;
    private Instant rotatedAt;
}
