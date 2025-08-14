package com.ecommerce.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        boolean ok = username != null && !username.isBlank() && password != null && !password.isBlank();
        if (!ok) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
        return ResponseEntity.ok(Map.of(
                "username", username,
                "token", "dummy-token-" + username
        ));
    }
}


