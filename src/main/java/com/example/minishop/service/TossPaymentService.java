package com.example.minishop.service;

import com.example.minishop.entity.Order;
import com.example.minishop.entity.Payment;
import com.example.minishop.repository.OrderRepository;
import com.example.minishop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final String tossApiBaseUrl = "https://api.tosspayments.com/v1/payments/ready";
    private final String secretKey = "test_sk_24xLea5zVA9M25JoqJExVQAMYNwW"; // 테스트 시크릿키

    public String requestPayment(Long orderId, BigDecimal amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String productName = order.getOrderItems().isEmpty() ? "상품명 없음" :
                order.getOrderItems().get(0).getProduct().getName();

        Map<String, Object> body = new HashMap<>();
        body.put("orderId", order.getId().toString());
        body.put("orderName", productName);
        body.put("amount", amount.intValue());
        body.put("customerName", order.getMember().getRealName());
        body.put("successUrl", "http://localhost:3000/payment/success");
        body.put("failUrl", "http://localhost:3000/payment/fail");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tossApiBaseUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            String redirectUrl = (String) responseBody.get("nextRedirectUrl");  // ✅ 이게 핵심

            // 결제 기록은 실제 결제 승인되면 그때 저장
            return redirectUrl;
        } else {
            throw new RuntimeException("Toss payment request failed");
        }
    }


    public Payment confirmPayment(String paymentKey) {
        String confirmUrl = "https://api.tosspayments.com/v1/payments/" + paymentKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(confirmUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();

            // paymentKey, orderId, amount, etc. 필요하면 꺼내기
            String orderId = (String) responseBody.get("orderId");
            Integer amount = (Integer) responseBody.get("amount");

            // 주문 조회
            Order order = orderRepository.findById(Long.parseLong(orderId))
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // DB에 결제 성공 상태 업데이트 또는 새 결제 기록 생성
            Payment payment = paymentRepository.findByOrder(order)
                    .orElse(Payment.builder()
                            .order(order)
                            .amount(new BigDecimal(amount))
                            .paymentMethod("CARD") // 실제 결제 수단은 response에서 꺼내도 됨
                            .paymentDate(LocalDateTime.now())
                            .build());

            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            return payment;
        } else {
            throw new RuntimeException("Toss payment confirmation failed");
        }
    }
}
