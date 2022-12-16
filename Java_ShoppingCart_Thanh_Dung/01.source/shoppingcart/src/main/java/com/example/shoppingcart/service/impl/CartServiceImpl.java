package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.dto.CartDTO;
import com.example.shoppingcart.entity.BillDetail;
import com.example.shoppingcart.entity.Cart;
import com.example.shoppingcart.entity.Product;
import com.example.shoppingcart.exception.NotFoundException;
import com.example.shoppingcart.repository.CartRepository;
import com.example.shoppingcart.repository.ProductRepository;
import com.example.shoppingcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    private ProductRepository productRepository;
    private CartRepository cartRepository;

    private HttpSession session;

    @Autowired
    public CartServiceImpl(ProductRepository productRepository, CartRepository cartRepository, HttpSession session) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.session = session;
    }


    @Override
    public Map<String, Object> findCartByUserUserId() {
        Long userId = (Long) session.getAttribute("user");
        List<Cart> carts = cartRepository.findCartByUserUserId(userId);
        List<CartDTO> cartDTOList = new ArrayList<>();
        for (Cart cart:carts) {
            CartDTO cartDTO = new CartDTO();
            Long productId = cart.getProductId();

            cartDTO.setProductName(productRepository.getProductNameByProductId(productId));
            cartDTO.setProductPrice(productRepository.getProductPrice(productId));
            cartDTO.setQuantity(cart.getCartQuantity());

            cartDTOList.add(cartDTO);
        }
        Long totalPrice = getTotalPrice(userId);

        Map<String, Object> result = new LinkedHashMap<String,Object>();

        result.put("All products in cart", cartDTOList);
        result.put("Total price", totalPrice);
        return result;
    }

    @Override
    public void addProductToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product does not exist!"));
        if (quantity > product.getProductQuantity()) {
            quantity = product.getProductQuantity();
        }
        Cart cart = new Cart();
        cart.setCartQuantity(quantity);
        cart.setProductId(productId);
        cart.setUserId((Long) session.getAttribute("user"));

        Cart oldCart = cartRepository.findCartByProductIdAndUserId(cart.getProductId(), cart.getUserId());

        if (oldCart == null) {
            cartRepository.save(cart);
        } else {
            int newQuantity = oldCart.getCartQuantity() + cart.getCartQuantity();
            oldCart.setCartQuantity(newQuantity);
            cartRepository.save(oldCart);
        }
    }

    @Override
    public void updateCart(Long[] productId, int[] quantity) {
        for (int i = 0; i < productId.length; i++) {
            Cart cart = new Cart();
            cart.setCartQuantity(quantity[i]);
            cart.setProductId(productId[i]);
            cart.setUserId((Long) session.getAttribute("user"));
            Cart oldCart = cartRepository.findCartByProductIdAndUserId(cart.getProductId(), cart.getUserId());
            if (oldCart != null) {
                int newQuantity = oldCart.getCartQuantity() + cart.getCartQuantity();
                if (newQuantity <= 0) {
                    cartRepository.delete(oldCart);
                } else {
                    oldCart.setCartQuantity(newQuantity);
                    cartRepository.save(oldCart);
                }
            } else {
                throw new NotFoundException("Product does not exist in cart");
            }
        }
    }

    @Override
    @Transactional
    public void resetCart() {
        Long userId = (Long) session.getAttribute("user");
        List <Cart> carts = cartRepository.findCartByUserUserId(userId);
        if (!carts.isEmpty()) {
            cartRepository.deleteCartsByUserUserId(userId);
        }
        else throw new NotFoundException("No cart found!");
    }

    @Override
    public Long getTotalPrice(Long userId) {
        List<Cart> carts = cartRepository.findCartByUserUserId(userId);
        long totalPrice = 0;

        for (Cart cart : carts) {
            long productId = cart.getProductId();
            totalPrice += productRepository.getProductPrice(productId) * cart.getCartQuantity();
        }
        return totalPrice;
    }

    @Override
    public Set<BillDetail> convertToBillDetails() {
        Long user_id = (Long) session.getAttribute("user");
        Set<BillDetail> billDetailSet = new HashSet<>();
        List<Cart> carts = cartRepository.findCartByUserUserId(user_id);

        for (Cart cart : carts) {
            BillDetail billDetail = new BillDetail();
            Long product_id = cart.getProductId();

            billDetail.setBillDetailQuantity(cart.getCartQuantity());
            billDetail.setBillDetailPrice(productRepository.getProductPrice(product_id));
            billDetail.setProductId(product_id);

            billDetailSet.add(billDetail);
        }
        return billDetailSet;
    }
}
