package com.example.shoppingcart.controller;

import com.example.shoppingcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/viewcart")
    public ResponseEntity<?> viewCartByUserId() {
        return new ResponseEntity<Map<String,Object>>(cartService.findCartByUserUserId(), HttpStatus.OK);

    }

    @PostMapping("/add-cart")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<String> addProductToCart(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity
    ) {
        cartService.addProductToCart(productId, quantity);
        return ResponseEntity.ok("Successful");
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @PutMapping("/update-cart")
    public ResponseEntity<String> updateCart(
            @RequestParam("productId") Long[] productId,
            @RequestParam("quantity") int[] quantity
    ) {
        cartService.updateCart(productId, quantity);
        return ResponseEntity.ok("Cart has been updated");
    }


    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @DeleteMapping("/reset-cart")
    public ResponseEntity<String> resetCart() {
        cartService.resetCart();
        return ResponseEntity.ok("Cart has been reset");
    }
}
