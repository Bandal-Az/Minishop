package com.example.minishop.controller;

import com.example.minishop.dto.product.ProductRequestDto;
import com.example.minishop.dto.product.ProductResponseDto;
import com.example.minishop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 전체 상품 조회 - 모두 접근 가능
    @GetMapping
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllProducts();
    }

    // ID로 특정 상품 조회 - 모두 접근 가능
    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // 새 상품 생성 - 인증된 사용자만 (또는 관리자만)
    @PreAuthorize("hasRole('ADMIN')")  // 관리자 권한 필요 시
    @PostMapping
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto dto) {
        return productService.createProduct(dto);
    }

    // 기존 상품 수정 - 인증된 사용자만 (또는 관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProductResponseDto updateProduct(@PathVariable Long id, @RequestBody ProductRequestDto dto) {
        return productService.updateProduct(id, dto);
    }

    // 상품 삭제 - 인증된 사용자만 (또는 관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    // Fakestoreapi에서 상품 불러와서 저장
    @PostMapping("/fetch-external")
    public String fetchExternalProducts() {
        productService.fetchAndSaveProductsFromApi();
        return "Fakestoreapi 상품 저장 완료";
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDto> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }
}

