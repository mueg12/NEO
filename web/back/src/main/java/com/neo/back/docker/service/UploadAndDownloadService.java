package com.neo.back.docker.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neo.back.springjwt.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadAndDownloadService {
    
    public String upload(MultipartFile[] files){
        User user = null;
        saveUserFolder_file(files,user);
        
        return "";
    }

    private void saveUserFolder_file(MultipartFile[] files, User user){
            String userId = "null";  // 차후에 수정하도록 해야함
            Path basePath = Paths.get("").toAbsolutePath().resolve("src/main/resources/test/"+userId);
            for (MultipartFile file : files) {
                String userFilePath = file.getOriginalFilename();
                String folderPathStr = "";
                int lastIndex = userFilePath.lastIndexOf('/');
                if (lastIndex != -1) {
                    // 상대 경로에서 폴더 경로를 추출
                    folderPathStr = userFilePath.substring(0, lastIndex);
                } 
                Path folderPath = basePath.resolve(folderPathStr);
                Path filePath = basePath.resolve(userFilePath);
                try {
                    // 새로운 사용자 경로에 해당하는 모든 부모 폴더를 생성
                    Files.createDirectories(folderPath);
                    System.out.println(folderPath);
                    Files.write(filePath, file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    }
            }
    }
}
