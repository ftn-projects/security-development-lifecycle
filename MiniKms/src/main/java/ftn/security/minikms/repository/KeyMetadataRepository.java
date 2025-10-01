package ftn.security.minikms.repository;

import ftn.security.minikms.entity.KeyMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface KeyMetadataRepository extends JpaRepository<KeyMetadata, UUID> {
    boolean existsByIdAndUserUsername(UUID id, String username);
    Optional<KeyMetadata> findByIdAndUserUsername(UUID id, String username);
}
