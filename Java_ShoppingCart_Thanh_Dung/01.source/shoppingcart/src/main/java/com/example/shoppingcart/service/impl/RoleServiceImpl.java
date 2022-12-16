package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.dto.RoleDTO;
import com.example.shoppingcart.entity.Role;
import com.example.shoppingcart.exception.DuplicateRecordException;
import com.example.shoppingcart.exception.NotFoundException;
import com.example.shoppingcart.repository.RoleRepository;
import com.example.shoppingcart.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean addRole(RoleDTO roleDTO) {
        if (this.roleRepository.countId(roleDTO.getRoleId()) < 1 &&
                this.roleRepository.countName(roleDTO.getRoleName()) < 1
        ) {
            this.roleRepository.addRole(roleDTO.getRoleId(), roleDTO.getRoleName());
            return true;
        }
        throw new DuplicateRecordException("Duplicate role id or role name");
    }

    @Override
    public boolean deleteRoleById(Long role_id) {
        if (this.roleRepository.countId(role_id) == 1) {
            // this.roleRepository.deleteRoleById(role_id);
            roleRepository.deleteById(role_id);
            return true;
        }
        throw new NotFoundException("Role " + role_id + " not exists");
    }
}
