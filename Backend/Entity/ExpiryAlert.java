package com.example.Medicine.Drug_OS.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "expiry_alerts")
@Data
@NoArgsConstructor
public class ExpiryAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private String message;

    @Column(name = "days_until_expiry", nullable = false)
    private Integer daysUntilExpiry;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "alert_generated_at", nullable = false)
    private LocalDateTime alertGeneratedAt = LocalDateTime.now();

    public ExpiryAlert(Integer productId, String message, Integer daysUntilExpiry) {
        this.productId = productId;
        this.message = message;
        this.daysUntilExpiry = daysUntilExpiry;
    }
}