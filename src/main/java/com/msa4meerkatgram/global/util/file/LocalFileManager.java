package com.msa4meerkatgram.global.util.file;

import com.msa4meerkatgram.global.errors.custom.FileManagedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocalFileManager {
    private final FileConfig fileConfig;

    /**
     * 파일 확장자 추출
     * @param file
     * @return String
     */
    public String extractExtension(MultipartFile file) {
        // 파일 존재 체크
        if (file == null || file.isEmpty()) {
            throw new FileManagedException("파일 저장 실패: 파일 확장자 추출 실패(파일 없음)");
        }

        // 파일 확장자 검증
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            throw new FileManagedException("파일 저장 실패: 파일 확장자 추출 실패(확장자 없음)");
        }

        String extractedExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        // 허용하는 확장자인지 검증
        if (!fileConfig.allowExtensionList().contains("image/" + extractedExtension)) {
            throw new FileManagedException("파일 저장 실패: 지원하지 않는 파일 확장자");
        }

        return extractedExtension;
    }

    /**
     * 파일 이름 생성
     * @return String - <code>yyyyMMdd_UUID</code>
     */
    public String generateFileName() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now();

        return now.format(dateFormatter) + "_" + UUID.randomUUID();
    }

    public String generateProfilePath(MultipartFile file) {
        return fileConfig.profilePath() + "/" + generateFileName() + "." + extractExtension(file);
    }

    public String generatePostPath(MultipartFile file) {
        return fileConfig.postPath() + "/" + generateFileName() + "." + extractExtension(file);
    }

    public boolean makeDir(Path targetPath) {
        try {
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }
            return true;
        } catch (IOException | IllegalStateException e) {
            return false;
        }
    }

    public void saveFile(MultipartFile file, String logicalPath) {
        try {
            // 절대 경로로 합성 (OS 구분자 자동 보정)
            Path physicalPath = Paths.get(fileConfig.storagePath(), logicalPath).normalize();

            // 디렉토리 확인
            if (!this.makeDir(physicalPath.getParent())) {
                throw new FileManagedException(
                        String.format("파일 저장 실패: 디렉토리 생성 실패 (경로: %s)", physicalPath.getParent())
                );
            }

            // 파일 저장
            file.transferTo(physicalPath.toFile());
        } catch (IOException | IllegalStateException e) {
            throw new FileManagedException(
                    String.format("파일 저장 실패: 파일 쓰기 작업 실패 (파일명: %s)", logicalPath)
            );
        }
    }
}
