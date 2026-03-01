package com.example.Medicine.Drug_OS.Reposit;

import com.example.Medicine.Drug_OS.Entity.ExpiryAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpiryAlertRepository extends JpaRepository<ExpiryAlert, Long> {
    List<ExpiryAlert> findByStatus(String status);
    List<ExpiryAlert> findByProductId(Integer productId);

}
