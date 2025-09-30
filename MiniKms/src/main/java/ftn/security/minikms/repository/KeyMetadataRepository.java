package ftn.security.minikms.repository;

import ftn.security.minikms.entity.KeyMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KeyMetadataRepository extends JpaRepository<KeyMetadataEntity, UUID> {
}
