package ftn.security.minikms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CryptoDTO {
    private String message;
    private UUID keyId;
    private String username;
    private Integer version;
    private String hmacBase64;
}
