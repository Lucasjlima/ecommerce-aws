package com.app.ecommerce.product.mapper;

import com.app.ecommerce.product.dto.request.CategoryRequest;
import com.app.ecommerce.product.dto.response.CategoryResponse;
import com.app.ecommerce.product.entity.Category;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryMapper {

    public static Category toEntity(CategoryRequest categoryRequest) {
        return Category.
                builder()
                .name(categoryRequest.name())
                .slug(categoryRequest.slug())
                .build();
    }

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.
                builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .build();
    }
}
