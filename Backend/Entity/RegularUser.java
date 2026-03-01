package com.example.Medicine.Drug_OS.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REGULAR")
public class RegularUser extends User {

    public RegularUser() { super(); }

    public RegularUser(String name, String username, String email, String password,
                       String phoneNumber, String address) {
        super(name, username, email, password, phoneNumber, address, "REGULAR");
    }
}
