package ftn.security.minikms.controller;

import ftn.security.minikms.dto.KeyDTO;
import ftn.security.minikms.dto.KeyMapper;
import ftn.security.minikms.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.security.InvalidParameterException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/keys")
public class KeyManagementController {
    private final KeyService keyService;
    private final KeyMapper mapper;

    @Autowired
    public KeyManagementController(KeyService keyService, KeyMapper mapper) {
        this.keyService = keyService;
        this.mapper = mapper;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createKey(@RequestBody KeyDTO dto) throws GeneralSecurityException {
        try {
            var created = keyService.createKey(dto.getAlias(), dto.getKeyType(), dto.getAllowedOperations());
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/rotate")
    public ResponseEntity<?> rotateKey(@RequestBody KeyDTO dto) throws GeneralSecurityException {
        try {
            var created = keyService.rotateKey(dto.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> deleteKey(@PathVariable UUID id) {
        try {
            keyService.deleteKey(id);
            return ResponseEntity.noContent().build();
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
