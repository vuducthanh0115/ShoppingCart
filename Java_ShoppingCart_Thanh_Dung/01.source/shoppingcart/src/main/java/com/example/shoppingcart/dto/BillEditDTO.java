package com.example.shoppingcart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillEditDTO {
    @NotNull(message = "Bill id must not be null")
    Long billId;

    String[] name_products;

    int[] quantities;
}
