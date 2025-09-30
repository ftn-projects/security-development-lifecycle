package ftn.security.minikms.dto;

import ftn.security.minikms.entity.KeyMetadataEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeyMapper {
    KeyMetadataDTO toDto(KeyMetadataEntity entity);
}
