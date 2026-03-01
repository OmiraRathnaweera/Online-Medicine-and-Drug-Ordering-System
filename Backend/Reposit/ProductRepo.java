package com.example.Medicine.Drug_OS.Reposit;

import com.example.Medicine.Drug_OS.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
    Optional<Product> findByProductName(String productName);

    List<Product> findByProductCategoryContainingIgnoreCase(String category);
    List<Product> findByProductNameContainingIgnoreCase(String name);

    boolean existsByProductName(String productName);
    @Query(value = "SELECT * FROM Product p " +
            "WHERE p.expiry_date IS NOT NULL " +
            "  AND DATEDIFF(p.expiry_date, :scanDate) <= p.expiry_threshold_days",
            nativeQuery = true)
    List<Product> findProductsNearingExpiry(@Param("scanDate") LocalDate scanDate);
}
