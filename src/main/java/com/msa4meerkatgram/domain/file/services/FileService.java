package com.msa4meerkatgram.domain.file.services;

import com.msa4meerkatgram.domain.file.responses.FileResponse;
import com.msa4meerkatgram.global.util.file.FileConfig;
import com.msa4meerkatgram.global.util.file.LocalFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final LocalFileManager localFileManager;
    private final FileConfig fileConfig;

    /**
     * 프로필 이미지 저장
     * @param file MultipartFile
     * @return FileResponse (저장된 파일의 URI)
     */
    public FileResponse storeProfile(MultipartFile file) {
        String filePath = localFileManager.generateProfilePath(file);

        // 파일 저장
        localFileManager.saveFile(file, filePath);

        return FileResponse.builder()
                .fileUri(fileConfig.serverUri() + filePath)
                .build();
    }

    public FileResponse storePost(MultipartFile file) {
        String filePath = localFileManager.generatePostPath(file);

        // 파일 저장
        localFileManager.saveFile(file, filePath);

        return FileResponse.builder()
                .fileUri(fileConfig.serverUri() + filePath)
                .build();
    }
}
