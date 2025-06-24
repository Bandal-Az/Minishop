package com.example.minishop.controller;

import com.example.minishop.dto.payment.PaymentRequestDto;
import com.example.minishop.dto.payment.PaymentResponseDto;
import com.example.minishop.service.PaymentService;
import com.example.minishop.service.TossPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final TossPaymentService tossPaymentService;

    // 전체 결제 조회
    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        List<PaymentResponseDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // 결제 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long id) {
        PaymentResponseDto payment = paymentService.getPaymentById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }

    // 결제 생성 (토스 결제 완료 후 저장용)
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto requestDto) {
        PaymentResponseDto createdPayment = paymentService.createPayment(requestDto);
        return ResponseEntity.ok(createdPayment);
    }

    // 토스 결제 요청
    @PostMapping("/toss/request")
    public ResponseEntity<String> requestTossPayment(@RequestParam Long orderId, @RequestParam BigDecimal amount) {
        String paymentKey = tossPaymentService.requestPayment(orderId, amount);
        return ResponseEntity.ok(paymentKey);
    }

    // 토스 결제 확인
    @PostMapping("/toss/confirm")
    public ResponseEntity<?> confirmTossPayment(@RequestParam String paymentKey) {
        try {
            return ResponseEntity.ok(tossPaymentService.confirmPayment(paymentKey));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
