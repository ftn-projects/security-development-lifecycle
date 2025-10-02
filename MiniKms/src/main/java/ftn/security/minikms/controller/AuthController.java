package ftn.security.minikms.controller;

import ftn.security.minikms.dto.AuthDTO;
import ftn.security.minikms.dto.TokenDTO;
import ftn.security.minikms.repository.UserRepository;
import ftn.security.minikms.service.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Autowired
    public AuthController(
            UserRepository userRepository,
            AuthenticationManager authManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> auth(@RequestBody AuthDTO dto) {
        try {
            var username = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            ).getName();

            var user = userRepository.findByUsername(username).orElseThrow(() ->
                    new IllegalStateException("Authenticated user not found in database"));

            var token = jwtService.generateToken(username, user.getId(), user.getRole());
            return ResponseEntity.ok(new TokenDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
