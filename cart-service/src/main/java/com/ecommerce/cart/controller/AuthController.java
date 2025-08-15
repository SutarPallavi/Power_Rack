package com.ecommerce.cart.controller;

import com.ecommerce.cart.model.Customer;
import com.ecommerce.cart.repo.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final CustomerRepository customers;

    public AuthController(CustomerRepository customers) {
        this.customers = customers;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String address = body.get("address");
        String password = body.get("password");
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "name, email, and password are required"));
        }
        if (customers.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email already registered"));
        }
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setAddress(address);
        c.setPassword(password);
        customers.save(c);
        return ResponseEntity.ok(Map.of("id", c.getId(), "email", c.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String identifier = body.get("username") != null ? body.get("username").trim() : null;
        String password = body.get("password") != null ? body.get("password").trim() : null;
        if (identifier == null || identifier.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
        return customers.findByEmail(identifier)
                .<ResponseEntity<?>>map(c -> {
                    if (!password.equals(c.getPassword())) {
                        return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
                    }
                    return ResponseEntity.ok(Map.of("username", c.getEmail(), "token", "dummy-token-" + c.getEmail()));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "User not found")));
    }
}


