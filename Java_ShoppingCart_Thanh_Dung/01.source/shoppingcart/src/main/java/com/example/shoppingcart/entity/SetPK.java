package com.example.shoppingcart.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
public class SetPK implements Serializable {
    protected Long productId;
    protected Long userId;
}
