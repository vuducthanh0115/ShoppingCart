package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.*;
import com.example.shoppingcart.entity.User;
import com.example.shoppingcart.payload.LoginRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {
    List<UserListDTO> getAllUser(Integer pageNo, Integer pageSize);

    List<UserListDTO> findAllByFullName(UserListDTO userListDTO);

    boolean createUser(UserDTO userDTO);

    void editUser(UserEditDTO userEditDTO, Long userId);

    void addRoleAccount(AddRoleDTO addRoleDTO, Long userId);

    boolean deleteUserById(Long userId);

    String forgotPassword(EmailDTO emailDTO) throws MessagingException;

    String changePassword(ForgotPasswordDTO forgotPassworDTO);

    String confirmSignUp(SignUpDTO signUpDTO);

    Map<String, String> login(User user);

    String getNewAccessToken(HttpServletRequest request);

    String getUsernameFromJWT(String token);
}
