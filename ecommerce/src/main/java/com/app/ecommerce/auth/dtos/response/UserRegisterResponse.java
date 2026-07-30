package com.app.ecommerce.auth.dtos.response;

import lombok.Builder;

import java.util.UUID;
@Builder
public record UserRegisterResponse(UUID id, String name, String email) {
}
