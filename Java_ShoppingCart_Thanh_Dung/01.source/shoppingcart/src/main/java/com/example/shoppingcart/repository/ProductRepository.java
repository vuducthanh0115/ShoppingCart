package com.example.shoppingcart.repository;

import com.example.shoppingcart.dto.ProductListDTO;
import com.example.shoppingcart.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Query(value = "DELETE FROM Product p WHERE p.productId = ?1")
    public void deleteByProductId(Long productId);

    @Query(value = "SELECT COUNT(p) FROM Product p WHERE p.productId = ?1")
    public int countId(Long product_id);

    @Query(value = "SELECT p FROM Product p WHERE p.productName = '%?1%'")
    public List<Product> getProductByName(String product_name, Pageable pageable);

    @Query(value = "SELECT p FROM Product p WHERE p.productId = ?1")
    public ProductListDTO getProductById(Long productId);

    @Query ("SELECT p.productPrice FROM Product p WHERE p.productId = ?1")
    Long getProductPrice(Long productId);

    @Query ("SELECT p.productName FROM Product p WHERE p.productId = ?1")
    String getProductNameByProductId(Long productId);

    @Query("SELECT p.productId FROM Product p WHERE p.productName = ?1")
    Long getProductIdByProductName(String product_name);
}
