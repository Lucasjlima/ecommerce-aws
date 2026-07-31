package com.app.ecommerce.auth.controller;

import com.app.ecommerce.auth.dtos.request.UserLoginRequest;
import com.app.ecommerce.auth.dtos.request.UserRegisterRequest;
import com.app.ecommerce.auth.dtos.response.LoginTokenResponse;
import com.app.ecommerce.auth.dtos.response.UserRegisterResponse;
import com.app.ecommerce.auth.entity.User;
import com.app.ecommerce.auth.mapper.UserMapper;
import com.app.ecommerce.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        User user = authService.register(UserMapper.toEntity(request));
        return ResponseEntity
                .created(URI.create("/api/v1/auth/" + user.getId()))
                .body(UserMapper.toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginTokenResponse> login(@RequestBody @Valid UserLoginRequest request) {
        LoginTokenResponse token =  authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/roles/{userEmail}")
    public ResponseEntity<Void> promoteToAdmin(@PathVariable String userEmail) {
        authService.promoteUserToAdmin(userEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles/{userEmail}")
    public ResponseEntity<Boolean> isUserAdmin(@PathVariable String userEmail) {
        return ResponseEntity.ok(authService.isUserAdmin(userEmail));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserRegisterResponse>> getAll() {
        return ResponseEntity.ok(authService.getAllUsers()
                        .stream()
                        .map(UserMapper::toResponse)
                        .toList());
    }
}
