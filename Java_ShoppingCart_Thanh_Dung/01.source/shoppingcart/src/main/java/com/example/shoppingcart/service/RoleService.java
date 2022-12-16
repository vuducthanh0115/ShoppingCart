package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.RoleDTO;
import com.example.shoppingcart.entity.Role;

public interface RoleService {
    public boolean addRole(RoleDTO roleDTO);

    public boolean deleteRoleById(Long role_id);
}
