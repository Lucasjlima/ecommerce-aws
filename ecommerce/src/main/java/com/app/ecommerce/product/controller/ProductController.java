package com.app.ecommerce.product.controller;

import com.app.ecommerce.product.dto.request.ProductRequest;
import com.app.ecommerce.product.dto.request.ProductUpdateRequest;
import com.app.ecommerce.product.dto.response.ProductResponse;
import com.app.ecommerce.product.dto.response.ProductUpdateResponse;
import com.app.ecommerce.product.service.ProductImageService;
import com.app.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest productRequest) {
        ProductResponse productResponse = productService.create(productRequest);
        return ResponseEntity
                .created(URI.create("/api/v1/products/" + productResponse.id()))
                .body(productResponse);
    }

    @PostMapping(value = "/upload/{productId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> uploadImage(@PathVariable UUID productId,
                                            @RequestParam("file") MultipartFile file) throws IOException {
        productImageService.upload(productId, file);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductUpdateResponse> update(
            @PathVariable UUID id, @RequestBody @Valid ProductUpdateRequest productUpdateRequest) {
        return ResponseEntity.ok(productService.update(id, productUpdateRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }
}
