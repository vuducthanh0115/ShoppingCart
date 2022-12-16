package com.example.shoppingcart;

import com.example.shoppingcart.service.FilesStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class ShoppingcartApplication implements CommandLineRunner {
    @Resource
    FilesStorageService storageService;
    public static void main(String[] args) {
        SpringApplication.run(ShoppingcartApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //storageService.deleteAll();
        //storageService.init();
    }
}
