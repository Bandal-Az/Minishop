package com.example.minishop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // '/uploads/**' URL 패턴으로 들어오는 요청을 'C:/upload/' 디렉토리에서 찾도록 매핑합니다.
        // 예를 들어, '/uploads/abc.jpg' 요청은 'C:/upload/abc.jpg' 파일을 찾게 됩니다.
        // 이 경로는 운영체제에 맞게 수정해야 하며, 해당 디렉토리가 존재하고 애플리케이션이 읽기/쓰기 권한을 가지고 있어야 합니다.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/upload/"); // Windows 경로 예시
        // Linux/macOS 환경이라면: .addResourceLocations("file:///home/user/upload/");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // API 경로 패턴
                .allowedOrigins("http://localhost:3000") // 허용할 프론트엔드 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}