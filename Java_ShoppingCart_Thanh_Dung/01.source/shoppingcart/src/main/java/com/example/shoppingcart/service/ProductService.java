package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.ProductListDTO;
import com.example.shoppingcart.entity.Product;
import com.example.shoppingcart.entity.ProductMedia;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductService {
    List<ProductListDTO> getAllProduct(Integer pageNo, Integer pageSize, String sortBy);

    boolean deleteProductById(Long product_id);

    void editProduct(ProductListDTO productListDTO, Long product_id);

    List<ProductListDTO> getProductByName(String product_name, Integer pageNo, Integer pageSize);

    boolean createProduct(ProductListDTO productListDTO);

    Set<ProductMedia> uploadProductMedia(ProductListDTO ProductDTO, MultipartFile[] multipartFile);

    ProductListDTO addAProduct(ProductListDTO productDTO, MultipartFile[] multipartFile);
    List<ProductListDTO> getById(Long productId,HttpSession session);
}
