package com.team9.anicare.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile, String dirName) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();

        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new CustomException(ResultCode.EMPTY_FILE_NAME);
        }

        // 이미지 파일인지 검사
        if(!isValidExtension(originalFileName)) {
            throw new CustomException(ResultCode.INVALID_FILE_EXTENSION);
        }

        // 파일명에 UUID 추가
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("\\s", "_");

        String fileName = dirName + "/" + uniqueFileName;

        // 파일에 대한 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // S3에 파일 업로드
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String fileName) {
        // 원래 파일 이름만 추출
        String splitFilename = ".com/";
        String originalFileName = fileName.substring(fileName.lastIndexOf(splitFilename) + splitFilename.length());

        // 특수문자 처리
        String decodedFileName = URLDecoder.decode(originalFileName, StandardCharsets.UTF_8);

        // 파일 삭제
        amazonS3Client.deleteObject(bucket, decodedFileName);
    }

    public String updateFile(MultipartFile newFile, String oldFileName, String dirName) throws IOException {
        // 기존에 파일이 존재하면
        if(oldFileName != null && !oldFileName.isEmpty()) {
            deleteFile(oldFileName);
        }

        // 새 파일 업로드
        return uploadFile(newFile, dirName);
    }

    private boolean isValidExtension(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

        return allowedExtensions.contains(fileExtension);
    }
}
