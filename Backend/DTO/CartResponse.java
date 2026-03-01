package com.example.Medicine.Drug_OS.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private List<CartItemDTO> items;
    private BigDecimal totalPrice;
    private int totalItems;
}