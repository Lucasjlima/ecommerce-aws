package com.app.ecommerce.auth.security;

import com.app.ecommerce.auth.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticatedUserProvider {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        return (User) authentication.getPrincipal();
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
