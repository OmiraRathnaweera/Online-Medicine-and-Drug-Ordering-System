package com.example.Medicine.Drug_OS.Reposit;

import com.example.Medicine.Drug_OS.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}