package ftn.security.minikms.controller;

import ftn.security.minikms.service.CryptoOperationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crypto")
public class CryptoController {

    private final CryptoOperationService cryptoService;

    public CryptoController(CryptoOperationService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostMapping("/sign")
    public ResponseEntity<String> sign(@RequestParam UUID keyId,
                                       @RequestBody byte[] data,
                                       Principal principal) {
        try {
            var signature = cryptoService.sign(keyId, data, principal.getName());
            return ResponseEntity.ok(Base64.getEncoder().encodeToString(signature));
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/mac/generate")
    public ResponseEntity<String> generateMac(@RequestParam UUID keyId,
                                              @RequestBody byte[] message,
                                              Principal principal) {
        try {
            var mac = cryptoService.generateMac(keyId, message, principal.getName());
            return ResponseEntity.ok(Base64.getEncoder().encodeToString(mac));
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/mac/verify")
    public ResponseEntity<Boolean> verifyMac(@RequestParam UUID keyId,
                                             @RequestBody byte[] message,
                                             @RequestParam String mac,
                                             Principal principal) {
        try {
            var macBytes = Base64.getDecoder().decode(mac);
            boolean valid = cryptoService.verifyMac(keyId, message, macBytes, principal.getName());
            return ResponseEntity.ok(valid);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/verify-signature")
    public ResponseEntity<Boolean> verifySignature(@RequestParam UUID keyId,
                                                   @RequestBody byte[] message,
                                                   @RequestParam String signature) {
        try {
            var sigBytes = Base64.getDecoder().decode(signature);
            boolean valid = cryptoService.verifySignature(keyId, message, sigBytes);
            return ResponseEntity.ok(valid);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}
