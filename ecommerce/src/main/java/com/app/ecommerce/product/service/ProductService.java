package com.app.ecommerce.product.service;

import com.app.ecommerce.product.entity.Category;
import com.app.ecommerce.product.entity.Product;
import com.app.ecommerce.product.repository.CategoryRepository;
import com.app.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product create(Product product) {

        Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(
                () -> new RuntimeException("Category not found.")
        );
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product not found with id: " + id)
        );
    }
}
