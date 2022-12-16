package com.example.shoppingcart.dto;

import com.example.shoppingcart.entity.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductListDTO {
    private Long productId;

    @NotEmpty(message = "Product name can not be empty")
    @Size(max = 50, min = 5, message = "Length: 5 -> 50 characters")
    private String productName;

    @NotNull(message = "Price can not be empty")
    private Long productPrice;

    @NotNull(message = "Quantity can not be empty")
    private Integer productQuantity;

    @NotNull(message = "Rating can not be empty")
    private Float productRating;

    @NotNull(message = "Category id can not be empty")
    private Long categoryId;

    private Long views;

    @NotNull(message = "sold number can not be empty")
    private Integer soldNumber;

    private Set<Rating> ratings;

    private Set<ProductMedia> productMediaSet;

    private Category category;

    private Set<Cart> carts;

    private Set<BillDetail> billDetails;
}
