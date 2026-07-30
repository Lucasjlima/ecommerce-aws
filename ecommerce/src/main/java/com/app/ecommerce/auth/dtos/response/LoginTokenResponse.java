package com.app.ecommerce.auth.dtos.response;

import lombok.Builder;

@Builder
public record LoginTokenResponse(String token, long expiresIn) {}
