package com.example.shoppingcart.repository;

import com.example.shoppingcart.entity.Views;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewsRepository extends JpaRepository<Views, Long> {
    //@Query(value ="SELECT v FROM Views v WHERE v.userId = ?1 AND v.productId = ?2 ")
    Views findByProductIdAndUserId(Long productId, Long userId);
}
