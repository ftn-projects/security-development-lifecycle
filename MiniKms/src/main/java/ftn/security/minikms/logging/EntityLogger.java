package ftn.security.minikms.logging;

import jakarta.persistence.*;

public class EntityLogger {

    @PrePersist
    public void prePersist(Object entity) {
        System.out.println("Creating: " + entity);
    }

    @PostPersist
    public void postPersist(Object entity) {
        System.out.println("Created: " + entity);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        System.out.println("Updating: " + entity);
    }

    @PostUpdate
    public void postUpdate(Object entity) {
        System.out.println("Updated: " + entity);
    }

    @PreRemove
    public void preRemove(Object entity) {
        System.out.println("Deleting: " + entity);
    }

    @PostRemove
    public void postRemove(Object entity) {
        System.out.println("Deleted: " + entity);
    }
}
