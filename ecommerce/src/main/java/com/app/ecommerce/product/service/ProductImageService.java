package com.app.ecommerce.product.service;

import com.app.ecommerce.aws.service.S3ImageService;
import com.app.ecommerce.product.entity.Product;
import com.app.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final S3ImageService s3ImageService;
    private final ProductRepository productRepository;
    private final Tika tika;

    public void upload(UUID productId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String mimeType = tika.detect(file.getInputStream());

        if (!ALLOWED_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("File type not allowed: " + mimeType);
        }

        String key = "products/" + productId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        s3ImageService.uploadImg(key, file.getBytes(), file.getContentType());
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found")
        );
        product.setImgKey(key);
        productRepository.save(product);
    }

    public String generatePresignedUrl(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        );
        return s3ImageService.generatePresignedUrl(product.getImgKey());
    }


    private final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );
}
