package com.example.shoppingcart.dto;

import com.example.shoppingcart.entity.Product;
import com.example.shoppingcart.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    private Long ratingId;

    @NotNull(message = "Vote can not be null")
    //@Size(min = 1, max = 5, message = "vote must be in range 1-5")
    @Min(value = 1, message = "vote must be in range 1-5")
    @Max(value = 5, message = "vote must be in range 1-5")
    private Integer vote;

    @NotNull(message = "productId can not be null")
    private Long productId;

    private Long userId;

    private User user;

    private Product product;
}
