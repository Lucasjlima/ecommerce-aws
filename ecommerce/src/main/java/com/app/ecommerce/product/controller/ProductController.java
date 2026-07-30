package com.app.ecommerce.product.controller;

import com.app.ecommerce.product.dto.request.ProductRequest;
import com.app.ecommerce.product.dto.response.ProductResponse;
import com.app.ecommerce.product.entity.Product;
import com.app.ecommerce.product.mapper.ProductMapper;
import com.app.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest productRequest) {
        Product product = productService.create(ProductMapper.toEntity(productRequest));
        return ResponseEntity
                .created(URI.create("/api/v1/products/" + product.getId()))
                .body(ProductMapper.toResponse(product));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity
                .ok(productService.findAll()
                        .stream()
                        .map(ProductMapper::toResponse)
                        .toList());
    }
}
