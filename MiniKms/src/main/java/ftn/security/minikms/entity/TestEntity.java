package ftn.security.minikms.entity;

import ftn.security.minikms.logging.EntityLogger;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@EntityListeners(EntityLogger.class)
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
