package com.example.Medicine.Drug_OS.Service;

import com.example.Medicine.Drug_OS.Entity.ExpiryAlert;
import com.example.Medicine.Drug_OS.Entity.Product;
import com.example.Medicine.Drug_OS.Reposit.ExpiryAlertRepository;
import com.example.Medicine.Drug_OS.Reposit.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@EnableScheduling
public class AlertService {

    private final ProductRepo productRepo;
    private final ExpiryAlertRepository alertRepository;

    public AlertService(ProductRepo productRepo, ExpiryAlertRepository alertRepository) {
        this.productRepo = productRepo;
        this.alertRepository = alertRepository;
    }

    @Scheduled(fixedRate = 500000)
    public void generateExpiryAlerts() {
        LocalDate scanDate = LocalDate.now();
        List<Product> productsToAlert = productRepo.findProductsNearingExpiry(scanDate);

        for (Product product : productsToAlert) {
            if (product.getExpiryDate() == null) continue;

            long daysLeft = ChronoUnit.DAYS.between(scanDate, product.getExpiryDate());
            if (daysLeft < 0) continue; // already expired - optionally handle separately

            boolean pendingAlertExists = alertRepository.findByProductId(product.getProductId())
                    .stream()
                    .anyMatch(alert -> "PENDING".equals(alert.getStatus()));

            if (!pendingAlertExists) {
                String message = String.format("%s is expiring in %d days.", product.getProductName(), daysLeft);
                ExpiryAlert newAlert = new ExpiryAlert(product.getProductId(), message, (int) daysLeft);
                alertRepository.save(newAlert);
            }
        }
    }

    public Optional<ExpiryAlert> createManualOverrideAlert(int productId, String customMessage) {
        return productRepo.findById(productId).map(product -> {
            LocalDate scanDate = LocalDate.now();
            long daysLeft = product.getExpiryDate() == null ? -1 : ChronoUnit.DAYS.between(scanDate, product.getExpiryDate());
            String finalMessage = String.format("MANUAL OVERRIDE: %s (Days left: %d). Reason: %s",
                    product.getProductName(), daysLeft, customMessage);

            ExpiryAlert newAlert = new ExpiryAlert(product.getProductId(), finalMessage, (int) daysLeft);
            return alertRepository.save(newAlert);
        });
    }

    public List<ExpiryAlert> getAllPendingAlerts() {
        return alertRepository.findByStatus("PENDING");
    }

    public Optional<ExpiryAlert> applyDiscount(Long alertId, Double discountRate) {
        if (discountRate == null) return Optional.empty();
        return alertRepository.findById(alertId).flatMap(alert ->
                productRepo.findById(alert.getProductId()).map(product -> {
                    double newPrice = product.getProductPrice() * (1 - (discountRate / 100.0));
                    product.setProductPrice(newPrice);
                    product.setIsDiscounted(true);
                    productRepo.save(product);

                    alert.setStatus(String.format("ACTIONED - DISCOUNT APPLIED (%.2f%%)", discountRate));
                    return alertRepository.save(alert);
                })
        );
    }

    public boolean removeItemFromStock(Long alertId) {
        Optional<ExpiryAlert> alertOpt = alertRepository.findById(alertId);

        if (alertOpt.isEmpty()) {
            return false;
        }

        ExpiryAlert alert = alertOpt.get();

        Optional<Product> productOpt = productRepo.findById(alert.getProductId());
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setProductQuantity(0);
            product.setIsDiscounted(false);
            productRepo.save(product);

            alert.setStatus("ACTIONED - STOCK SET TO ZERO (REMOVED FROM SALE)");
            alertRepository.save(alert);
            return true;
        } else {
            alert.setStatus("ACTIONED - PRODUCT NOT FOUND (CANNOT REMOVE)");
            alertRepository.save(alert);
            return true;
        }
    }
}
