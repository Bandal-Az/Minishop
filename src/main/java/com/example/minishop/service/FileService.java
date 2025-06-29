package com.example.minishop.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")  // application.properties에서 경로 설정 가능
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + File.separator + fileName);

        try {
            Files.createDirectories(path.getParent()); // 디렉토리가 없다면 생성
            file.transferTo(path);
            return "/uploads/" + fileName;  // 저장된 파일의 URL 반환
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
