package ftn.security.minikms.controller;

import ftn.security.minikms.dto.SignRequestDTO;
import ftn.security.minikms.dto.VerifyRequestDTO;
import ftn.security.minikms.service.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/signatures")
public class SignatureController {

    @Autowired
    private SignatureService signatureService;

    @PostMapping("/sign")
    @Transactional(readOnly = true)
    public ResponseEntity<String> sign(@RequestParam UUID keyId,
                                       @RequestBody SignRequestDTO request) {
        try {
            byte[] signature = signatureService.sign(keyId, request.getMessage(), request.getVersion());
            return ResponseEntity.ok(Base64.getEncoder().encodeToString(signature));
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    @Transactional(readOnly = true)
    public ResponseEntity<String> verify(@RequestParam UUID keyId,
                                         @RequestParam(required = false) Integer version,
                                         @RequestBody VerifyRequestDTO req) {
        try {
            boolean valid = signatureService.verify(
                    keyId,
                    req.getMessage(),
                    Base64.getDecoder().decode(req.getSignature()),
                    version
            );
            return ResponseEntity.ok(valid ? "VALID" : "INVALID");
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
