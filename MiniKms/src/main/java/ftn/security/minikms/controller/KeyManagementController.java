package ftn.security.minikms.controller;

import ftn.security.minikms.dto.KeyDTO;
import ftn.security.minikms.dto.KeyMapper;
import ftn.security.minikms.service.KeyManagementService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/keys")
public class KeyManagementController {
    private final KeyManagementService keyService;
    private final KeyMapper mapper;

    @Autowired
    public KeyManagementController(KeyManagementService keyService) {
        this.keyService = keyService;
        this.mapper = Mappers.getMapper(KeyMapper.class);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createKey(@RequestBody KeyDTO dto, Principal principal) throws GeneralSecurityException {
        var username = principal.getName();

        try {
            var created = keyService.createKey(dto.getAlias(), dto.getKeyType(), username);
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

    @GetMapping
    public ResponseEntity<?> getKeyMetadata() {
        var keys = keyService.getAllKeys();
        return ResponseEntity.ok(keys.stream().map(mapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getKeyById(@PathVariable UUID id) {
        try {
            var key = keyService.getKeyById(id);
            return ResponseEntity.ok(mapper.toDto(key));
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKey(@PathVariable UUID id) {
        try {
            keyService.deleteKey(id);
            return ResponseEntity.noContent().build();
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
