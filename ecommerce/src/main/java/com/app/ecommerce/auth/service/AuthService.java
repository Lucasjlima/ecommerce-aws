package com.app.ecommerce.auth.service;

import com.app.ecommerce.auth.dtos.request.UserLoginRequest;
import com.app.ecommerce.auth.dtos.response.LoginTokenResponse;
import com.app.ecommerce.auth.entity.RoleEntity;
import com.app.ecommerce.auth.entity.RoleEnumType;
import com.app.ecommerce.auth.entity.User;
import com.app.ecommerce.auth.exceptions.BadRequestException;
import com.app.ecommerce.auth.repository.RoleRepository;
import com.app.ecommerce.auth.repository.UserRepository;
import com.app.ecommerce.auth.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Value("${app.jwt.expiration}")
    private long expirationTime;

    @Transactional
    public User register(User user) throws BadRequestException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        RoleEntity roleEntity = roleRepository.findByName(RoleEnumType.ROLE_DEFAULT_USER.name())
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(roleEntity);
        return userRepository.save(user);

    }

    public LoginTokenResponse login(UserLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        String token = tokenProvider.generateToken(authentication);
        return LoginTokenResponse
                .builder()
                .token(token)
                .expiresIn(expirationTime)
                .build();

    }

    @Transactional
    public void promoteUserToAdmin(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new RuntimeException("User not found with email")
        );

        RoleEntity roleEntity = roleRepository.findByName(RoleEnumType.ROLE_ADMIN.name()).orElseThrow(
                () -> new RuntimeException("Default role not found")
        );
        user.getRoles().add(roleEntity);
        userRepository.save(user);

    }

    public boolean isUserAdmin(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new RuntimeException("User not found with email")
        );

        RoleEntity roleEntity = roleRepository.findByName(RoleEnumType.ROLE_ADMIN.name()).orElseThrow(
                () -> new RuntimeException("Default role not found")
        );

        return user.getRoles().contains(roleEntity);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
