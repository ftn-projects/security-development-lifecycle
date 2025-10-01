package ftn.security.minikms.logging;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Slf4j
public class EntityLogger {

    @Value("${logging.entity.enabled:true}")
    private boolean loggingEnabled;

    private void logEntity(String action, String phase, Object entity) {
        if (!loggingEnabled) return;

        log.info("{}", Map.of(
                "action", action,
                "phase", phase,
                "entity", entity.getClass().getSimpleName()
        ));
    }

    @PrePersist
    public void prePersist(Object entity) { logEntity("create", "pre", entity); }

    @PostPersist
    public void postPersist(Object entity) { logEntity("create", "post", entity); }

    @PreUpdate
    public void preUpdate(Object entity) { logEntity("update", "pre", entity); }

    @PostUpdate
    public void postUpdate(Object entity) { logEntity("update", "post", entity); }

    @PreRemove
    public void preRemove(Object entity) { logEntity("delete", "pre", entity); }

    @PostRemove
    public void postRemove(Object entity) { logEntity("delete", "post", entity); }
}
