package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.ProductListDTO;
import com.example.shoppingcart.entity.Product;
import com.example.shoppingcart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/view-all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ProductListDTO>> getAllProduct(@RequestParam(defaultValue = "0") Integer pageNo,
                                                           @RequestParam Integer pageSize,
                                                           @RequestParam(defaultValue = "productRating") String sortBy) {
        return ResponseEntity.ok(productService.getAllProduct(pageNo, pageSize, sortBy));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductListDTO productListDTO) {
        productService.createProduct(productListDTO);
        return ResponseEntity.ok("Add product successful");
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> editProduct(@Valid @RequestBody ProductListDTO productListDTO,
                                         @PathVariable (value = "id") Long product_id) {
        productService.editProduct(productListDTO, product_id);
        return ResponseEntity.ok("Update Successful");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteProductById(@PathVariable (value = "id") Long product_id) {
        productService.deleteProductById(product_id);
        return ResponseEntity.ok("Product id = " + product_id + " deleted!");
    }

    @GetMapping("/view-all-with-name")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<List<ProductListDTO>> getAllProductWithName(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                      @RequestParam Integer pageSize,
                                                                      @RequestParam (value = "name") String product_name) {
        return ResponseEntity.ok(productService.getProductByName(product_name, pageNo, pageSize));
    }

    @GetMapping("/view-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    ResponseEntity<List<ProductListDTO>> getAllProductWithId(@PathVariable("id") Long id, HttpSession session) {
            return  ResponseEntity.ok(productService.getById(id, session));
    }

    @PostMapping("/add-product")
    public ResponseEntity<String> createAProduct(ProductListDTO ProductDTO,@RequestParam(name = "files",
            required = false) MultipartFile[] multipartFile){
        productService.addAProduct(ProductDTO,multipartFile);
        return ResponseEntity.status(HttpStatus.OK).body("Successfull");

    }

    @PostMapping("/add-file")
    public ResponseEntity<String> createFileProduct(ProductListDTO ProductDTO,@RequestParam(name = "files",
            required = false) MultipartFile[] multipartFile){
        productService.uploadProductMedia(ProductDTO,multipartFile);
        return ResponseEntity.status(HttpStatus.OK).body("Successfull");

    }
}
