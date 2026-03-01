package com.example.Medicine.Drug_OS.Service;

import com.example.Medicine.Drug_OS.Reposit.ExpiryAlertRepository;
import com.example.Medicine.Drug_OS.Reposit.ProductRepo;
import com.example.Medicine.Drug_OS.Reposit.UserRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final ProductRepo productRepo;
    private final ExpiryAlertRepository alertRepository;

    public DashboardService(UserRepository userRepository, ProductRepo productRepo, ExpiryAlertRepository alertRepository) {
        this.userRepository = userRepository;
        this.productRepo = productRepo;
        this.alertRepository = alertRepository;
    }

    public Map<String, Long> getAdminDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalProducts", productRepo.count());
        stats.put("pendingAlerts", (long) alertRepository.findByStatus("PENDING").size());
        return stats;
    }
}
