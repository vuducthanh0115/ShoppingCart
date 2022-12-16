package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.service.FilesStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {
    private final Path root = Paths.get("uploads");
    @Override
    //@PostConstruct
    public void init() {
        /*try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }*/
    }
    @Override
    public String getPathFile(MultipartFile file, String subFolder) {
        String filename = file.getOriginalFilename();
        return root + "\\" + subFolder + "\\" + filename;
    }

    @Override
    public String save(MultipartFile file, String subFolder) {
        String savingPath;
        try {
            File theDir = new File(root + "\\" + subFolder);
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String filename = file.getOriginalFilename();
            savingPath = root + "\\" + subFolder + "\\" + filename;
            Files.copy(file.getInputStream(), this.root.resolve(subFolder + "\\" + filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
        return savingPath;
    }
    @Override
    public void deleteAll() {

        FileSystemUtils.deleteRecursively(root.toFile());
    }


    public String getFileTypeByProbeContentType(String fileName) {
        String fileType = "Undetermined";
        final File file = new File(fileName);
        try {

            fileType = Files.probeContentType(file.toPath());

        } catch (IOException ioException) {
            System.out.println("File type not detected for " + fileName);
        }
        return fileType;

    }


    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
