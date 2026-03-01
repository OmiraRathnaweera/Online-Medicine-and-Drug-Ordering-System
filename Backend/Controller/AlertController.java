package com.example.Medicine.Drug_OS.Controller;

import com.example.Medicine.Drug_OS.Entity.ExpiryAlert;
import com.example.Medicine.Drug_OS.Service.AlertService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/pending")
    public List<ExpiryAlert> getPendingAlerts() {
        return alertService.getAllPendingAlerts();
    }

    @PostMapping("/manual")
    public ResponseEntity<ExpiryAlert> createManualAlert(@RequestBody ManualAlertCreateRequest requestBody) {
        return alertService.createManualOverrideAlert(requestBody.getProductId(), requestBody.getMessage())
                .map(alert -> ResponseEntity.status(HttpStatus.CREATED).body(alert))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/discount/{alertId}")
    public ResponseEntity<ExpiryAlert> applyDiscountAction(@PathVariable Long alertId, @RequestBody DiscountUpdateRequest requestBody) {
        return alertService.applyDiscount(alertId, requestBody.getDiscountRate())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/remove/{alertId}")
    public ResponseEntity<Void> removeItemFromStockAction(@PathVariable Long alertId) {
        boolean success = alertService.removeItemFromStock(alertId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Data
    static class DiscountUpdateRequest {
        private Double discountRate;
    }

    @Data
    static class ManualAlertCreateRequest {
        private int productId;
        private String message;
    }
}
