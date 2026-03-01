package com.example.Medicine.Drug_OS.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDTO {
    private Integer productId;
    private String productName;
    private Double productPrice;
    private String productCategory;
    private Integer productQuantity;
    private Integer lowStockThreshold;
    private String productStatus;
    private LocalDate expiryDate;
    private Integer expiryThresholdDays;
    private Boolean isDiscounted;
}
