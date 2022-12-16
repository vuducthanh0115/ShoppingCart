package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.dto.*;
import com.example.shoppingcart.entity.Role;
import com.example.shoppingcart.entity.User;
import com.example.shoppingcart.exception.DuplicateRecordException;
import com.example.shoppingcart.exception.NotFoundException;
import com.example.shoppingcart.payload.LoginRequest;
import com.example.shoppingcart.repository.RoleRepository;
import com.example.shoppingcart.repository.UserRepository;
import com.example.shoppingcart.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    @Override
    public List<UserListDTO> getAllUser(Integer pageNo, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<UserListDTO> pageResult = userRepository.findAll(paging)
                .stream()
                .map(data -> {
                    UserListDTO userListDTO = new UserListDTO();
                    BeanUtils.copyProperties(data, userListDTO);
                    return userListDTO;
                }).collect(Collectors.toList());
        if (!pageResult.isEmpty()) {
            return pageResult;
        }
        throw new NotFoundException("User not found");
    }

    @Override
    public List<UserListDTO> findAllByFullName(UserListDTO userDTO) {
        List<UserListDTO> users = userRepository.findAllByFullName(userDTO.getUserFullname()).stream()
                .map(data -> {
                    UserListDTO userListDTO = new UserListDTO();
                    BeanUtils.copyProperties(data, userListDTO);
                    return userListDTO;
                }).collect(Collectors.toList());
        if (!users.isEmpty()) {
            return users;
        }
        throw new NotFoundException("User not found with fullname : " + userDTO.getUserFullname());
    }

    @Override
    public boolean createUser(UserDTO userDTO) {
        if (
                userRepository.countUsername(userDTO.getUsername()) < 1 &&
                        userRepository.countEmail(userDTO.getUserEmail()) < 1 &&
                        userRepository.countPhone(userDTO.getUserPhone()) < 1
        ) {
            Set<Role> roleDefault = new HashSet<>();
            roleDefault.add(roleRepository.findById(2L).orElse(null));
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userDTO.setRoles(roleDefault);
            userDTO.setCheck(false);
            User user = new User();
            BeanUtils.copyProperties(userDTO, user);
            userRepository.save(user);

            //send email to confirm sign up
            try {
                String email = userDTO.getUserEmail();
                UUID token = UUID.randomUUID();
                user.setToken(String.valueOf(token));
                user.setCreatedAt(LocalDateTime.now());
                userRepository.save(user);
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper;

                mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                mimeMessageHelper.setFrom(sender);
                mimeMessageHelper.setTo(email);
                mimeMessageHelper.setText("TOKEN : " + token +
                        "<br>Or <a href='fb.com'>click here</a> to change pw. " +
                        "<br>Token valid for 10 minutes", true);
                mimeMessageHelper.setSubject("Confirmed Sign Up Email");

                javaMailSender.send(mimeMessage);
            } catch (MessagingException e) {
                System.out.println("Send mail error!");
            }
            return true;
        }
        throw new DuplicateRecordException("Duplicate account or phone number or email");
    }

    @Override
    public void editUser(UserEditDTO userEditDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User does not exist"));
        user.setUserFullname(userEditDTO.getUserFullname());
        user.setUserAddress(userEditDTO.getUserAddress());
        user.setUserEmail(userEditDTO.getUserEmail());
        user.setUserGender(userEditDTO.getUserGender());
        user.setUserPhone(userEditDTO.getUserPhone());
        user.setUsername(userEditDTO.getUsername());
        userRepository.save(user);
    }

    @Override
    public void addRoleAccount(AddRoleDTO addRoleDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User does not exist"));
        Set<String> strrole = addRoleDTO.getRoles();
        Set<Role> roles = user.getRoles();
        strrole.forEach(role -> {
            switch (role) {
                case "ADMIN" -> {
                    Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN");
                    roles.add(adminRole);
                }
                case "USER" -> {
                    Role userRole = roleRepository.findByRoleName("ROLE_USER");
                    roles.add(userRole);
                }
                case "EDITOR" -> {
                    Role editorRole = roleRepository.findByRoleName("ROLE_EDITOR");
                    roles.add(editorRole);
                }
                case "MODERATOR" -> {
                    Role moderatorRole = roleRepository.findByRoleName("ROLE_MODERATOR");
                    roles.add(moderatorRole);
                }
                case "ADVERTISER" -> {
                    Role advertiserRole = roleRepository.findByRoleName("ROLE_ADVERTISER");
                    roles.add(advertiserRole);
                }
                default -> {
                }
            }
        });
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public boolean deleteUserById(Long userId) {
        if (userRepository.countId(userId) > 0) {
            userRepository.deleteUserById(userId);
            return true;
        }
        throw new NotFoundException("User does not exist");
    }

    @Override
    public String forgotPassword(EmailDTO emailDTO) throws MessagingException {
        if (userRepository.countEmail(emailDTO.getRecipient()) > 0) {
            User user = userRepository.findByUserEmail(emailDTO.getRecipient());
            UUID token = UUID.randomUUID();
            user.setToken(String.valueOf(token));
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper;

            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(emailDTO.getRecipient());
            mimeMessageHelper.setText("TOKEN : " + token +
                    "<br>Or <a href='#'>click here</a> to change pw. " +
                    "<br>Token valid for 10 minutes", true);
            mimeMessageHelper.setSubject(emailDTO.getSubject());

            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }
        throw new RuntimeException("Email does not exist");
    }

    @Override
    public String changePassword(ForgotPasswordDTO forgotPassworDTO) {
        User user = userRepository.findByToken(forgotPassworDTO.getToken());
        if (user != null) {
            if (
                    forgotPassworDTO.getToken() != null &&
                            !passwordEncoder.matches(forgotPassworDTO.getPassword(), user.getPassword())
            ) {
                User editUser = userRepository.findByToken(forgotPassworDTO.getToken());
                editUser.setPassword(passwordEncoder.encode(forgotPassworDTO.getPassword()));
                editUser.setToken(null);
                userRepository.save(editUser);
                return "Password Change Successful";
            }
            throw new RuntimeException("Reset password should not same as old password condition");
        }

        throw new RuntimeException("Token does not exist");
    }

    @Override
    public String confirmSignUp(SignUpDTO signUpDTO) {
        User user = userRepository.findByTokenLogin(signUpDTO.getToken());
        if (user != null) {
            if (signUpDTO.getToken() != null &&
                    !user.isCheckStatus()
            ) {
                User editUser = userRepository.findByTokenLogin(signUpDTO.getToken());
                editUser.setCheckStatus(true);
                editUser.setToken(null);
                userRepository.save(editUser);
            } else if (user.isCheckStatus()) {
                return "User is enable to buy products!";
            }
            return "Authenticate done!";
        }
        throw new RuntimeException("Token does not exist");
    }

    private final String JWT_SECRET = "shoppingcart";

    @Override
    public Map<String, String> login(User user) {
        Date now = new Date();
        String access_token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 600000L))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
        String refresh_token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 604800000L))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
        Map<String, String> token = new HashMap<String, String>();
        token.put("username", user.getUsername());
        token.put("access token", access_token);
        token.put("refresh token", refresh_token);
        return token;
    }

    @Override
    public String getNewAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String jwt = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            jwt = bearerToken.substring(7);
            String username = getUsernameFromJWT(jwt);
            Date now = new Date();
            String access_token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + 600000L))
                    .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                    .compact();
            return access_token;
        }
            throw new RuntimeException("Fail");
    }

    @Override
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
