package com.app.ecommerce.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${app.aws.region}")
    private String region;

    @Value("${app.aws.secret.key}")
    private String secretKey;

    @Value("${app.aws.access.key}")
    private String accessKey;

    @Bean
    public StaticCredentialsProvider credentialsProvider() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );
    }

    @Bean
    public S3Client s3Client(StaticCredentialsProvider credentialsProvider) {
        return S3Client
                .builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    S3Presigner s3Presigner(StaticCredentialsProvider credentialsProvider) {
        return S3Presigner
                .builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }


}
