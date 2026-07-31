package com.app.ecommerce.product.mapper;

import com.app.ecommerce.product.dto.request.ProductRequest;
import com.app.ecommerce.product.dto.response.ProductResponse;
import com.app.ecommerce.product.entity.Product;
import lombok.experimental.UtilityClass;

import java.time.Instant;

@UtilityClass
public class ProductMapper {
    public static Product toEntity(ProductRequest productRequest) {
        return Product.
                builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .stockQuantity(productRequest.stockQuantity())
                .createdAt(Instant.now())
                .build();
    }

    public static ProductResponse toResponse(Product product) {
        return ProductResponse.
                builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory().getId())
                .imgKey(product.getImgKey())
                .build();
    }
}
