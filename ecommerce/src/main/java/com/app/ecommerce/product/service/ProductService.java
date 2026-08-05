package com.app.ecommerce.product.service;

import com.app.ecommerce.product.dto.request.ProductRequest;
import com.app.ecommerce.product.dto.response.ProductResponse;
import com.app.ecommerce.product.entity.Category;
import com.app.ecommerce.product.entity.Product;
import com.app.ecommerce.product.mapper.ProductMapper;
import com.app.ecommerce.product.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponse create(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.categoryId()).orElseThrow(
                () -> new NotFoundException("Category not found.")
        );
        Product product = ProductMapper.toEntity(productRequest);
        product.setCategory(category);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + id)
        );
    }
}
