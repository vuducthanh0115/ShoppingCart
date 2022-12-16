package com.example.shoppingcart.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {
    void init();
    String save(MultipartFile file, String subFolder);

    String getFileTypeByProbeContentType(String fileName);
    Resource load(String filename);
    void deleteAll();
    Stream<Path> loadAll();

    String getPathFile(MultipartFile file, String subFolder);
}
