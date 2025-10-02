package ftn.security.minikms.controller;

import ftn.security.minikms.dto.CryptoDTO;
import ftn.security.minikms.service.KeyComputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping(value = "/api/v1/crypto")
public class KeyComputeController {
    private final KeyComputeService service;
    @Autowired
    public KeyComputeController(KeyComputeService service) {
        this.service = service;
    }

    @PostMapping("/encrypt/symmetric")
    public ResponseEntity<?> encryptSymmetric(@RequestBody CryptoDTO dto) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        try {
            String encrypted = service.encryptAes(dto.getMessage(), dto.getKeyId(), dto.getVersion());
            return ResponseEntity.status(HttpStatus.CREATED).body(encrypted);
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/decrypt/symmetric")
    public ResponseEntity<?> decryptSymmetric(@RequestBody CryptoDTO dto) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        try {
            String decrypted = service.decryptAes(dto.getMessage(), dto.getKeyId(), dto.getVersion());
            return ResponseEntity.status(HttpStatus.CREATED).body(decrypted);
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/encrypt/asymmetric")
    public ResponseEntity<?> encryptAsymmetric(@RequestBody CryptoDTO dto) throws
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        try {
            String encrypted = service.encryptRsa(dto.getMessage(), dto.getKeyId(), dto.getVersion());
            return ResponseEntity.status(HttpStatus.CREATED).body(encrypted);
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/decrypt/asymmetric")
    public ResponseEntity<?> decryptAsymmetric(@RequestBody CryptoDTO dto) throws
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        try {
            String decrypted = service.decryptRsa(dto.getMessage(), dto.getKeyId(), dto.getVersion());
            return ResponseEntity.status(HttpStatus.CREATED).body(decrypted);
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/compute/hmac")
    public ResponseEntity<?> computeHmac(@RequestBody CryptoDTO dto) throws Exception {
        try {
            String computed = service.computeHmac(dto.getMessage(), dto.getKeyId(), dto.getVersion());
            return ResponseEntity.status(HttpStatus.CREATED).body(computed);
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify/hmac")
    public ResponseEntity<?> verifyHmac(@RequestBody CryptoDTO dto) throws Exception {
        try {
            Boolean verified = service.verifyHmac(dto.getMessage(), dto.getHmacBase64(), dto.getKeyId(), dto.getVersion());
            return ResponseEntity.status(HttpStatus.CREATED).body(verified);
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
