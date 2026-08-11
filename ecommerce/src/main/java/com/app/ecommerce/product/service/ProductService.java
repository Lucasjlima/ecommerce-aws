package com.app.ecommerce.product.service;

import com.app.ecommerce.product.dto.request.ProductRequest;
import com.app.ecommerce.product.dto.request.ProductUpdateRequest;
import com.app.ecommerce.product.dto.response.ProductResponse;
import com.app.ecommerce.product.dto.response.ProductUpdateResponse;
import com.app.ecommerce.product.entity.Product;
import com.app.ecommerce.product.mapper.ProductMapper;
import com.app.ecommerce.product.repository.ProductRepository;
import com.app.ecommerce.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional
    public ProductResponse create(ProductRequest productRequest) {
        Product product = ProductMapper.toEntity(productRequest);
        product.setCategory(categoryService.findById(productRequest.categoryId()));
        return ProductMapper.toResponse(productRepository.save(product));
    }


    @Transactional
    public ProductUpdateResponse update(UUID id, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Product not found.")
        );

        if (productUpdateRequest.name() != null) product.setName(productUpdateRequest.name());

        if (productUpdateRequest.description() != null) product.setDescription(productUpdateRequest.description());

        if (productUpdateRequest.price() != null) product.setPrice(productUpdateRequest.price());

        if (productUpdateRequest.stockQuantity() != null)
            product.setStockQuantity(productUpdateRequest.stockQuantity());

        if (productUpdateRequest.categoryId() != null)
            product.setCategory(categoryService.findById(productUpdateRequest.categoryId()));

        return ProductMapper.toUpdateResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Product not found.")
        );
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(UUID id) {
        return ProductMapper.toResponse(productRepository.findByIdAndActiveTrue(id).orElseThrow(
                () -> new NotFoundException("Product not found.")
        ));
    }
}
