package com.example.shoppingcart.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeliveryDTO {
    private String deliveryTime;

    private String status;
}
