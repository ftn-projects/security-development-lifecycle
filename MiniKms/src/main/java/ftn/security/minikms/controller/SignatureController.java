package ftn.security.minikms.controller;

import ftn.security.minikms.service.SignatureService;
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

    private final SignatureService cryptoService;

    public SignatureController(SignatureService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Transactional(readOnly = true)
    @PostMapping("/sign")
    public ResponseEntity<String> sign(@RequestParam UUID keyId,
                                       @RequestBody byte[] data,
                                       Principal principal) {
        return null;
    }

    @PostMapping("/mac/generate")
    public ResponseEntity<String> generateMac(@RequestParam UUID keyId,
                                              @RequestBody byte[] message,
                                              Principal principal) {
        return null;
    }

    @PostMapping("/mac/verify")
    public ResponseEntity<Boolean> verifyMac(@RequestParam UUID keyId,
                                             @RequestBody byte[] message,
                                             @RequestParam String mac,
                                             Principal principal) {
    return null;
    }

    @PostMapping("/verify-signature")
    public ResponseEntity<Boolean> verifySignature(@RequestParam UUID keyId,
                                                   @RequestBody byte[] message,
                                                   @RequestParam String signature) {
    return null;
    }
}
