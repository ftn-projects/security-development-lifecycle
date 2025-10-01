package ftn.security.minikms;

import ftn.security.minikms.service.RootKeyManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
class MiniKmsApplicationTests {

    @Test
    void contextLoads() { }
}
