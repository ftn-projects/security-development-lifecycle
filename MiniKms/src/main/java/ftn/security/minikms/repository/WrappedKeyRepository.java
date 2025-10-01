package ftn.security.minikms.repository;

import ftn.security.minikms.entity.WrappedKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrappedKeyRepository extends JpaRepository<WrappedKey, Long> {
}
