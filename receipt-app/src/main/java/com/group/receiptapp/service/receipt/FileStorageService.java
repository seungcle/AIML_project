package com.group.receiptapp.service.receipt;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String UPLOAD_DIR = "uploads/"; // 저장 디렉터리

    public String saveFile(MultipartFile file) throws IOException {
        // 디렉터리가 존재하지 않으면 생성
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 파일 저장
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File targetFile = new File(UPLOAD_DIR + fileName);
        file.transferTo(targetFile);

        return targetFile.getAbsolutePath(); // 저장된 파일 경로 반환
    }
}