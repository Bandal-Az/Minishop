package com.example.minishop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 결제 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // 연관된 주문 정보  // 연관된 계약 정보

    @Column(nullable = false)
    private BigDecimal amount;  // 결제 금액 (단위: 원)

    @Column(nullable = false)
    private String paymentMethod;  // 결제 수단 (예: 카드, 계좌이체, 간편결제 등)

    @Column(nullable = false)
    private LocalDateTime paymentDate;  // 결제 일시

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;  // 결제 상태 (성공, 실패 등)

    public enum PaymentStatus {
        SUCCESS,
        FAILED,
        PENDING,
        CANCELLED
    }
}
