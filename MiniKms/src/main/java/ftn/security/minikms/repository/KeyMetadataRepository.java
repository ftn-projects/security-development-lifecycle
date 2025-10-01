package ftn.security.minikms.repository;

import ftn.security.minikms.entity.KeyMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KeyMetadataRepository extends JpaRepository<KeyMetadata, UUID> {
}
