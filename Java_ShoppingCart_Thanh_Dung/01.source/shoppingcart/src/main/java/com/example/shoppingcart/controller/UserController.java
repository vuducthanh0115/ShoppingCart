package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.*;
import com.example.shoppingcart.entity.User;
import com.example.shoppingcart.payload.LoginRequest;
import com.example.shoppingcart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/view-all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserListDTO>> getAllUser(@RequestParam(defaultValue = "0") Integer pageNo,
                                                        @RequestParam Integer pageSize) {
        return ResponseEntity.ok(userService.getAllUser(pageNo, pageSize));
    }

    @PostMapping("/find-all-by-fullname")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserListDTO>> findAllByFullName(@RequestBody UserListDTO userListDTO) {
        return ResponseEntity.ok(userService.findAllByFullName(userListDTO));
    }

    @PostMapping("/signup")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserDTO userDTO
    ) {
        userService.createUser(userDTO);
        return ResponseEntity.ok("Add Successful");
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> editUser(
            @Valid @RequestBody UserEditDTO userEditDTO,
            @PathVariable("id") Long id
    ) {
        userService.editUser(userEditDTO, id);
        return ResponseEntity.ok("Update Successful");
    }

    @PutMapping("/addrole/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> addRole(
            @RequestBody AddRoleDTO addRoleDTO,
            @PathVariable("id") Long id
    ) {
        userService.addRoleAccount(addRoleDTO, id);
        return ResponseEntity.ok("Add Role Successful");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(
            @PathVariable("id") Long id
    ) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Delete Successful");
    }

    @PostMapping("/forgotpassword")
    public String sendMail(@RequestBody EmailDTO details) throws MessagingException {
        String status = userService.forgotPassword(details);
        return status;
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ForgotPasswordDTO forgotPassworDto){
        userService.changePassword(forgotPassworDto);
        return ResponseEntity.ok("Password Change Successful");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@RequestBody SignUpDTO signUpDTO) {
        return ResponseEntity.ok(userService.confirmSignUp(signUpDTO));
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private HttpSession session;

    @PostMapping("/login")
    public ResponseEntity<String> authentication(@RequestBody LoginRequest loginRequest) throws BadCredentialsException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                session.setAttribute("user", user.getUserId());
                return ResponseEntity.ok("Authenticated Successful!");
            }
        }catch (BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The Username or Password is Incorrect");
        }
        return null;
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String,String>> getToken(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
                return ResponseEntity.ok(userService.login(user));
        }catch (BadCredentialsException e) {
            Map<String,String> map = new HashMap<String,String>();
            map.put("error","The Username or Password is Incorrect");
            return ResponseEntity.ok(map);
        }
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> getNewAccessToken(HttpServletRequest request) {
        String token = userService.getNewAccessToken(request);
        Map<String, String> map = new HashMap<String, String>();
        map.put("access token", token);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

}
