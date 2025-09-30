package ftn.security.minikms.controller;

import ftn.security.minikms.dto.KeyDTO;
import ftn.security.minikms.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(value = "/api/v1/keys")
public class KeyManagementController {
    @Autowired
    private KeyService keyService;

    @PostMapping("/create")
    public ResponseEntity<KeyDTO> createKey(@RequestBody KeyDTO dto) throws NoSuchAlgorithmException {
        String id = keyService.createKey(dto.getKeyType());
        dto.setId(id);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
    @PostMapping("/rotate")
    public ResponseEntity<KeyDTO> rotateKey(@RequestBody KeyDTO dto){
        keyService.rotateKey(dto.getKeyType(), dto.getId());
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
    @PutMapping("/delete/{id}")
    public ResponseEntity<String> deleteKey(@PathVariable String id){
        keyService.deleteKey(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

}
