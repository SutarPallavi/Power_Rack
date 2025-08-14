package com.ecommerce.cart.controller;

import com.ecommerce.cart.client.ProductClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final Map<String, List<Map<String, Object>>> carts = new HashMap<>();
    private final ProductClient productClient;

    public CartController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Map<String, Object>>> getCart(@PathVariable String username) {
        return ResponseEntity.ok(carts.getOrDefault(username, new ArrayList<>()));
    }

    @PostMapping("/{username}/items")
    public ResponseEntity<List<Map<String, Object>>> addItem(
            @PathVariable String username,
            @RequestBody Map<String, Object> item
    ) {
        // Optional validation: ensure product exists
        productClient.listProducts();
        carts.computeIfAbsent(username, k -> new ArrayList<>()).add(item);
        return ResponseEntity.ok(carts.get(username));
    }
}


