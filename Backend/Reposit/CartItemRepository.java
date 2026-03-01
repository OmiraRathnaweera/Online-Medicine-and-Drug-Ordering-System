package com.example.Medicine.Drug_OS.Reposit;


import com.example.Medicine.Drug_OS.Entity.CartItem;
import com.example.Medicine.Drug_OS.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct_ProductId(User user, Integer productId);
    void deleteByUser(User user);
}