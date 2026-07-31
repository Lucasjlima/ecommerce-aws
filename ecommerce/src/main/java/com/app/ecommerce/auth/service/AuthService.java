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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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
    public void promoteUserToAdmin(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        RoleEntity roleEntity = roleRepository.findByName(RoleEnumType.ROLE_ADMIN.name()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")
        );
        user.getRoles().add(roleEntity);
        userRepository.save(user);

    }

    public boolean isUserAdmin(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        for (RoleEntity roleEntity : user.getRoles()) {
            if (roleEntity.getName().equals(RoleEnumType.ROLE_ADMIN.name())) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
