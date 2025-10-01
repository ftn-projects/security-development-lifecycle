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
    void contextLoads() {

    }

    @Test
    void help() throws GeneralSecurityException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair pair = gen.generateKeyPair();

        byte[] original = pair.getPrivate().getEncoded();
        System.out.println("Original length = " + original.length);

        UUID id = UUID.randomUUID();
        int version = 1;

        RootKeyManager mgr = new RootKeyManager("onX6qBcMY+LtSTtDSWIS8xhnCkM8v/mozZhYL56dkf8=");
        byte[] wrapped = mgr.wrap(original, id, version);
        byte[] unwrapped = mgr.unwrap(wrapped, id, version);

        System.out.println("Unwrapped length = " + unwrapped.length);
        System.out.println(Arrays.equals(original, unwrapped)); // should be true

        PrivateKey restored = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(unwrapped));
        System.out.println("Restored format = " + restored.getFormat()); // should be "PKCS#8"

    }


}
