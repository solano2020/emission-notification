package org.microservices.notification_emission.infrastructure.output.database.oracle.Entity;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notification_emission", schema = "DB_USER")
public class NotificationEmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "channel")
    private String channel;

    @Column(name = "message")
    private String message;

    @Column(name = "status")
    private String status;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insurance_id", nullable = false)
    private Insurance insurance;

    protected NotificationEmission() {
    }

    public NotificationEmission(String channel, String status, String message) {
        this.channel = channel;
        this.status = status;
        this.message = message;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createAt == null) {
            createAt = now;
        }
        updateAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updateAt = LocalDateTime.now();
    }

}
