package ftn.security.minikms.dto;

import ftn.security.minikms.enumeration.KeyType;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class KeyDTO {
    private UUID id;
    private String alias;
    private KeyType keyType;
}
