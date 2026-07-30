package com.app.ecommerce.auth.mapper;

import com.app.ecommerce.auth.dtos.request.UserRegisterRequest;
import com.app.ecommerce.auth.dtos.response.UserRegisterResponse;
import com.app.ecommerce.auth.entity.User;
import lombok.experimental.UtilityClass;

import java.time.Instant;

@UtilityClass
public class UserMapper {
    public static User toEntity(UserRegisterRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .createdAt(Instant.now())
                .build();
    }

    public static UserRegisterResponse toResponse(User user) {
        return UserRegisterResponse
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }


}
