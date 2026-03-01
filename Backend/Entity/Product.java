package com.example.Medicine.Drug_OS.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @NotBlank(message = "Medicine name is required !")
    @Column(nullable = false, unique = true)
    private String productName;

    @NotNull(message = "Price is required!")
    @Column(nullable = false)
    private Double productPrice;

    @NotBlank(message = "Category is required !")
    private String productCategory;

    @NotNull(message = "Quantity is required !")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer productQuantity;

    @Column(nullable = false)
    private Integer lowStockThreshold = 5;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "expiry_threshold_days", nullable = false)
    private Integer expiryThresholdDays = 60;

    @Column(name = "is_discounted", nullable = false)
    private Boolean isDiscounted = false;

    @Transient
    public String getProductStatus() {
        if (productQuantity == null || productQuantity == 0) {
            return "OUT_OF_STOCK";
        } else if (productQuantity <= (lowStockThreshold == null ? 5 : lowStockThreshold)) {
            return "LOW_STOCK";
        } else {
            return "IN_STOCK";
        }
    }
}
