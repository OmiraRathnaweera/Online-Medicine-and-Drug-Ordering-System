package com.example.Medicine.Drug_OS.Service;

import com.example.Medicine.Drug_OS.Entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<Payment> getAllPayments();
    Payment getPaymentById(Long id);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
}
