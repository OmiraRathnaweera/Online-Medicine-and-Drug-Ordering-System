package com.example.Medicine.Drug_OS.Controller;

import com.example.Medicine.Drug_OS.Entity.Payment;
import com.example.Medicine.Drug_OS.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody Payment payment) {
        try {
            paymentService.createPayment(payment);
            return ResponseEntity.ok("Payment successful!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment failed: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public String listPayments(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        return "payment/payment-list";
    }
}