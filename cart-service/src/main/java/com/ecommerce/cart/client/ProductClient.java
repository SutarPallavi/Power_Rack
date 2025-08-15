package com.ecommerce.cart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "product-service", url = "http://localhost:8082")
public interface ProductClient {
    @GetMapping("/api/products")
    List<Map<String, Object>> listProducts();
}


