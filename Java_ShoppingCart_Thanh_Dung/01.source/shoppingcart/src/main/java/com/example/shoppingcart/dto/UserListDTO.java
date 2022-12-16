package com.example.shoppingcart.dto;

import com.example.shoppingcart.entity.Product;
import com.example.shoppingcart.entity.Role;
import com.example.shoppingcart.entity.Views;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserListDTO {

    private Long userId;

    private String username;

    private String userAddress;

    private String userFullname;

    private String userPhone;

    private String userEmail;

    private String userGender;

    private boolean check;

    private String token;

    private Set<Role> roles = new HashSet<>();

    private Set<Views> views;
}
