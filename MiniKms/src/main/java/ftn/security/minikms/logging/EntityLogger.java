package ftn.security.minikms.logging;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityLogger {

    @PrePersist
    public void prePersist(Object entity) {
        log.info("Creating: {}", entity);
    }

    @PostPersist
    public void postPersist(Object entity) {
        log.info("Created: {}", entity);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        log.info("Updating: {}", entity);
    }

    @PostUpdate
    public void postUpdate(Object entity) {
        log.info("Updated: {}", entity);
    }

    @PreRemove
    public void preRemove(Object entity) {
        log.info("Deleting: {}", entity);
    }

    @PostRemove
    public void postRemove(Object entity) {
        log.info("Deleted: {}", entity);
    }
}
