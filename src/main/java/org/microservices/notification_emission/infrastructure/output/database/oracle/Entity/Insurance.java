package org.microservices.notification_emission.infrastructure.output.database.oracle.Entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "insurance", schema = "DB_USER")
@Getter
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "policy")
    private String policy;

    @Column(name = "plaque")
    private String plaque;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Column(name = "customer_id")
    private String customerId;

    @OneToMany(
            mappedBy = "insurance",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = false
    )
    private List<NotificationEmission> notificationEmissions = new ArrayList<>();

    // Método helper para mantener consistencia
    public void addNotification(NotificationEmission notification) {
        notificationEmissions.add(notification);
        notification.setInsurance(this);
    }

}
