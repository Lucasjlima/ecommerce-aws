package com.app.ecommerce.product.service;

import com.app.ecommerce.product.dto.request.CategoryRequest;
import com.app.ecommerce.product.dto.response.CategoryResponse;
import com.app.ecommerce.product.entity.Category;
import com.app.ecommerce.product.mapper.CategoryMapper;
import com.app.ecommerce.product.repository.CategoryRepository;
import com.app.ecommerce.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CategoryRequest categoryRequest) {
        Category category = CategoryMapper.toEntity(categoryRequest);
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public Category findById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category not found with id: " + id)
        );
    }
}
