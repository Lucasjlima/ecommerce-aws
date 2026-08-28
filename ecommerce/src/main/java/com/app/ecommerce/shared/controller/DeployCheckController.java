package com.app.ecommerce.shared.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deploy-check")
public class DeployCheckController {

    @GetMapping
    public ResponseEntity<String> deployCheck() {
        return ResponseEntity.ok("Hello! The EC2 instance is executing!");
    }
}
