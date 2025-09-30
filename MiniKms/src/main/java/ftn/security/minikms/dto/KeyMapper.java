package ftn.security.minikms.dto;

import ftn.security.minikms.entity.KeyMetadataEntity;
import org.mapstruct.Mapper;

@Mapper
public interface KeyMapper {
    KeyMetadataDTO toDto(KeyMetadataEntity key);
}
