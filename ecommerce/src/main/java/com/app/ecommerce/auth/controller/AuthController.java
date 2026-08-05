package com.app.ecommerce.auth.controller;

import com.app.ecommerce.auth.dtos.request.UserLoginRequest;
import com.app.ecommerce.auth.dtos.request.UserRegisterRequest;
import com.app.ecommerce.auth.dtos.response.LoginTokenResponse;
import com.app.ecommerce.auth.dtos.response.UserRegisterResponse;
import com.app.ecommerce.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        UserRegisterResponse userRegisterResponse = authService.register(request);
        return ResponseEntity
                .created(URI.create("/api/v1/auth/" + userRegisterResponse.id()))
                .body(userRegisterResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginTokenResponse> login(@RequestBody @Valid UserLoginRequest request) {
        LoginTokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/roles/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> promoteToAdmin(@PathVariable UUID userId) {
        authService.promoteUserToAdmin(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> isUserAdmin(@PathVariable UUID userId) {
        return ResponseEntity.ok(authService.isUserAdmin(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserRegisterResponse>> getAll() {
        return ResponseEntity.ok(authService.getAllUsers());
    }
}
