package com.example.minishop.service;

import com.example.minishop.dto.payment.PaymentRequestDto;
import com.example.minishop.dto.payment.PaymentResponseDto;
import com.example.minishop.entity.Order;
import com.example.minishop.entity.Payment;
import com.example.minishop.entity.Payment.PaymentStatus;
import com.example.minishop.repository.OrderRepository;
import com.example.minishop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public PaymentResponseDto getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public PaymentResponseDto createPayment(PaymentRequestDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = Payment.builder()
                .order(order)
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .paymentDate(dto.getPaymentDate())
                .status(PaymentStatus.valueOf(dto.getStatus()))
                .build();

        Payment saved = paymentRepository.save(payment);
        return toResponseDto(saved);
    }

    private PaymentResponseDto toResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus().name())
                .build();
    }
}
