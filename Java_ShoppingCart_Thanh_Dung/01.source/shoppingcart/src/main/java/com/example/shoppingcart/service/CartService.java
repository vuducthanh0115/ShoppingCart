package com.example.shoppingcart.service;

import com.example.shoppingcart.entity.BillDetail;
import com.example.shoppingcart.entity.Cart;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CartService {

    Map<String, Object> findCartByUserUserId();
    void addProductToCart(Long productId, int quantity);

    void updateCart(Long[] productName, int[] quantity);

    void resetCart();

    Long getTotalPrice(Long userId);

    Set<BillDetail> convertToBillDetails();

}
