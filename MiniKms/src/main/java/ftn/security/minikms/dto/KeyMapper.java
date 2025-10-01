package ftn.security.minikms.dto;

import ftn.security.minikms.entity.KeyMetadata;
import org.mapstruct.Mapper;

@Mapper
public interface KeyMapper {
    KeyMetadataDTO toDto(KeyMetadata key);
}
