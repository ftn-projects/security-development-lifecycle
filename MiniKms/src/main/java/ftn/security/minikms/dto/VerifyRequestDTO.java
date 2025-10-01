package ftn.security.minikms.dto;

import lombok.Data;

@Data
public class VerifyRequestDTO {
    private String message;
    private String signature;
}
