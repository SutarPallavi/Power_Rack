package com.ecommerce.customer.controller;

import com.ecommerce.customer.model.Customer;
import com.ecommerce.customer.repo.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final CustomerRepository customers;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(CustomerRepository customers, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.customers = customers;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String identifier = body.get("username") != null ? body.get("username").trim() : null;
        String password = body.get("password") != null ? body.get("password").trim() : null;
        boolean hasValues = identifier != null && !identifier.isBlank() && password != null && !password.isBlank();
        if (!hasValues) {
            return ResponseEntity.badRequest().body(Map.of("error", "username and password are required"));
        }

        log.info("Auth login attempt for username/email='{}'", identifier);
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, password)
            );
            if (auth.isAuthenticated()) {
                return customers.findByEmailIgnoreCase(identifier)
                        .<ResponseEntity<?>>map(customer -> {
                            log.info("Auth login success for username/email='{}'", identifier);
                            return ResponseEntity.ok(Map.of(
                                    "username", customer.getEmail(),
                                    "token", "dummy-token-" + customer.getEmail()
                            ));
                        })
                        .orElseGet(() -> {
                            log.warn("Auth login failed post-auth: user not found for username/email='{}'", identifier);
                            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
                        });
            }
        } catch (BadCredentialsException ex) {
            log.warn("Auth login failed: bad credentials for username/email='{}'", identifier);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (Exception ex) {
            log.warn("Auth login failed: {} for username/email='{}'", ex.getClass().getSimpleName(), identifier);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
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
        if (customers.findByEmailIgnoreCase(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email already registered"));
        }
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setAddress(address);
        c.setPassword(passwordEncoder.encode(password));
        customers.save(c);
        return ResponseEntity.ok(Map.of("id", c.getId(), "email", c.getEmail()));
    }
}


