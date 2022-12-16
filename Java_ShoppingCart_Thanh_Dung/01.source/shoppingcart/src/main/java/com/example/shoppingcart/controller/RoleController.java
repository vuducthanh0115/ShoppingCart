package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.RoleDTO;
import com.example.shoppingcart.entity.Role;
import com.example.shoppingcart.service.impl.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleServiceImpl roleService;

    public RoleController(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> addRole(@Valid @RequestBody RoleDTO roleDTO) {
        this.roleService.addRole(roleDTO);
        return ResponseEntity.ok("Create role successful");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteRoleById(@PathVariable (value = "id") Long role_id) {
        this.roleService.deleteRoleById(role_id);
        return ResponseEntity.ok("Role " + role_id + " deleted!");
    }
}
