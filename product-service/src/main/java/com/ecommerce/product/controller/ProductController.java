package com.ecommerce.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listProducts() {
        List<Map<String, Object>> products = List.of(
                Map.of("id", 1, "name", "Laptop", "price", 999.99),
                Map.of("id", 2, "name", "Headphones", "price", 199.99),
                Map.of("id", 3, "name", "Keyboard", "price", 79.99)
        );
        return ResponseEntity.ok(products);
    }
}


