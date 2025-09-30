package ftn.security.minikms.repository;

import ftn.security.minikms.entity.WrappedKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrappedKeyRepository extends JpaRepository<WrappedKeyEntity, Long> {
}
