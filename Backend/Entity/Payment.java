package com.example.Medicine.Drug_OS.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "customer_name")
    private String customerName;

    private Double amount;

    private String method;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    // Constructors
    public Payment() {}

    public Payment(Integer orderId, String customerName, Double amount, String method, LocalDate paymentDate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
        this.method = method;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
}