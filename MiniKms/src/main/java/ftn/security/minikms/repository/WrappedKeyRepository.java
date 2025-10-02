package ftn.security.minikms.repository;

import ftn.security.minikms.entity.WrappedKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WrappedKeyRepository extends JpaRepository<WrappedKey, Long> {
    Optional<WrappedKey> findByMetadataIdAndVersion(UUID metadataId, Integer version);
}
