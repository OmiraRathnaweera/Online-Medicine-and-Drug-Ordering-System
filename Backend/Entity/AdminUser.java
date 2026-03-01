package com.example.Medicine.Drug_OS.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class AdminUser extends User {

    protected AdminUser() {
        super();
    }

    public AdminUser(String name, String username, String email, String password, String phoneNumber, String address) {
        super(name, username, email, password, phoneNumber, address, "admin");
    }
}
