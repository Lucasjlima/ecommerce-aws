package com.app.ecommerce.product.service;

import com.app.ecommerce.aws.service.S3ImageService;
import com.app.ecommerce.product.entity.Product;
import com.app.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final S3ImageService s3ImageService;
    private final ProductRepository productRepository;

    public void upload(UUID productId, MultipartFile multipartFile) throws IOException {
        String key = "products/" + productId + "/" + UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        s3ImageService.uploadImg(key, multipartFile.getBytes(), multipartFile.getContentType());
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException("Product not found")
        );
        product.setImgKey(key);
        productRepository.save(product);
    }

    public String generatePresignedUrl(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException("Product not found")
        );
        return s3ImageService.generatePresignedUrl(product.getImgKey());
    }
}
