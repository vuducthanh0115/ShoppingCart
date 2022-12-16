package com.example.shoppingcart.repository;

import com.example.shoppingcart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findCartByUserUserId(Long userId);

    Cart findCartByProductIdAndUserId(long productId, long userId);

    void deleteCartsByUserUserId(long userId);
}
