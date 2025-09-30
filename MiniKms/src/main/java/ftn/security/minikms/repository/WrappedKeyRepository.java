package ftn.security.minikms.repository;

import ftn.security.minikms.entity.WrappedKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WrappedKeyRepository extends JpaRepository<WrappedKeyEntity, Long> {
    Optional<WrappedKeyEntity> findFirstByLogicalKeyOrderByVersionDesc(UUID logicalKey);
}
