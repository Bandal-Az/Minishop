package com.example.minishop.service;

import com.example.minishop.dto.external.FakestoreProductDto;
import com.example.minishop.dto.product.ProductRequestDto;
import com.example.minishop.dto.product.ProductResponseDto;
import com.example.minishop.entity.Category;
import com.example.minishop.entity.Product;
import com.example.minishop.entity.ProductImage;
import com.example.minishop.repository.CategoryRepository;
import com.example.minishop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public ProductResponseDto createProduct(ProductRequestDto dto) {
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        }

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(category)
                .build();

        // 이미지 엔티티 생성 후 연관관계 설정
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                String url = dto.getImageUrls().get(i);
                ProductImage image = ProductImage.builder()
                        .imageUrl(url)
                        .isThumbnail(i == 0)  // 첫번째 이미지를 썸네일로 설정
                        .product(product)
                        .build();
                product.getImages().add(image);
            }
        }

        Product savedProduct = productRepository.save(product);

        return toResponseDto(savedProduct);
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setDescription(dto.getDescription());
                    existing.setPrice(dto.getPrice());
                    existing.setStock(dto.getStock());

                    if (dto.getCategoryId() != null) {
                        Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
                        existing.setCategory(category);
                    } else {
                        existing.setCategory(null);
                    }

                    // 이미지 업데이트 로직: 기존 이미지 삭제 후 새 이미지 세팅
                    existing.getImages().clear();

                    if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
                        for (int i = 0; i < dto.getImageUrls().size(); i++) {
                            String url = dto.getImageUrls().get(i);
                            ProductImage image = ProductImage.builder()
                                    .imageUrl(url)
                                    .isThumbnail(i == 0)
                                    .product(existing)
                                    .build();
                            existing.getImages().add(image);
                        }
                    }

                    return toResponseDto(productRepository.save(existing));
                })
                .orElse(null);
    }

    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream().map(ProductResponseDto::from).toList();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductResponseDto toResponseDto(Product product) {
        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .imageUrls(imageUrls)
                .build();
    }

    public void fetchAndSaveProductsFromApi() {
        String url = "https://fakestoreapi.com/products";

        FakestoreProductDto[] apiProducts = restTemplate.getForObject(url, FakestoreProductDto[].class);

        if (apiProducts != null) {
            for (FakestoreProductDto apiProduct : apiProducts) {
                // 카테고리 조회 또는 생성
                Category category = categoryRepository.findByName(apiProduct.getCategory())
                        .orElseGet(() -> {
                            Category newCategory = Category.builder()
                                    .name(apiProduct.getCategory())
                                    .build();
                            return categoryRepository.save(newCategory);
                        });

                // 중복 체크 (상품명 기준)
                boolean exists = productRepository.existsByName(apiProduct.getTitle());
                if (!exists) {
                    Product product = Product.builder()
                            .name(apiProduct.getTitle())
                            .description(apiProduct.getDescription())
                            .price(apiProduct.getPrice() != null ? apiProduct.getPrice() : BigDecimal.ZERO)
                            .stock(10) // 기본 재고
                            .category(category)
                            .isActive(true)
                            .build();

                    // 이미지 엔티티 생성 (단일 이미지, 썸네일로 설정)
                    ProductImage image = ProductImage.builder()
                            .imageUrl(apiProduct.getImage())
                            .isThumbnail(true)
                            .product(product)
                            .build();
                    product.getImages().add(image);

                    productRepository.save(product);
                }
            }
        }
    }
}

