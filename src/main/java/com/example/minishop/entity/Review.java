package com.example.minishop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 후기 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 어떤 상품에 대한 후기인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 작성자 (회원 = 구매자)

    @Column(nullable = false)
    private int rating; // 별점 (1~5점)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment; // 후기 내용

    @Column(name = "image_url")  // 새로 추가된 필드
    private String imageUrl; // 이미지 URL

}
