package com.example.Medicine.Drug_OS.Service;


import com.example.Medicine.Drug_OS.Entity.Payment;
import com.example.Medicine.Drug_OS.Reposit.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    public PaymentServiceImpl(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment createPayment(Payment payment) {
        // Set default order_id if not provided
        if (payment.getOrderId() == null) {
            payment.setOrderId(10001);
        }
        return repository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {
        Payment existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setOrderId(payment.getOrderId());
            existing.setCustomerName(payment.getCustomerName());
            existing.setAmount(payment.getAmount());
            existing.setMethod(payment.getMethod());
            existing.setPaymentDate(payment.getPaymentDate());
            return repository.save(existing);
        }
        return null;
    }

    @Override
    public void deletePayment(Long id) {
        repository.deleteById(id);
    }
}