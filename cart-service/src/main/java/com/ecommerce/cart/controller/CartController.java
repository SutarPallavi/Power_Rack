package com.ecommerce.cart.controller;

import com.ecommerce.cart.client.ProductClient;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.repo.CartItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartItemRepository cartRepo;
    private final ProductClient productClient;

    public CartController(CartItemRepository cartRepo, ProductClient productClient) {
        this.cartRepo = cartRepo;
        this.productClient = productClient;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable String username) {
        return ResponseEntity.ok(cartRepo.findByUsername(username));
    }

    @PostMapping("/{username}/items")
    @Transactional
    public ResponseEntity<List<CartItem>> addItem(
            @PathVariable String username,
            @RequestBody CartItem item
    ) {
        productClient.listProducts();

        Long productId = item.getProductId() != null ? item.getProductId() : item.getId();
        if (productId == null && item.getId() != null) {
            productId = item.getId();
        }

        CartItem entity = cartRepo.findByUsernameAndProductId(username, productId).orElse(null);
        if (entity != null) {
            int inc = item.getQty() != null ? item.getQty() : 1;
            entity.setQty((entity.getQty() != null ? entity.getQty() : 0) + inc);
            cartRepo.save(entity);
        } else {
            CartItem ci = new CartItem();
            ci.setUsername(username);
            ci.setProductId(productId);
            ci.setName(item.getName());
            ci.setQty(item.getQty() != null ? item.getQty() : 1);
            cartRepo.save(ci);
        }
        return ResponseEntity.ok(cartRepo.findByUsername(username));
    }

    @PostMapping("/{username}/items/{id}/increment")
    @Transactional
    public ResponseEntity<List<CartItem>> incrementItem(
            @PathVariable String username,
            @PathVariable("id") Long productId
    ) {
        CartItem entity = cartRepo.findByUsernameAndProductId(username, productId).orElse(null);
        if (entity == null) {
            entity = new CartItem();
            entity.setUsername(username);
            entity.setProductId(productId);
            entity.setQty(0);
        }
        entity.setQty(entity.getQty() + 1);
        cartRepo.save(entity);
        return ResponseEntity.ok(cartRepo.findByUsername(username));
    }

    @PostMapping("/{username}/items/{id}/decrement")
    @Transactional
    public ResponseEntity<List<CartItem>> decrementItem(
            @PathVariable String username,
            @PathVariable("id") Long productId
    ) {
        CartItem entity = cartRepo.findByUsernameAndProductId(username, productId).orElse(null);
        if (entity != null) {
            int qty = entity.getQty() != null ? entity.getQty() : 0;
            qty = qty - 1;
            if (qty <= 0) {
                cartRepo.delete(entity);
            } else {
                entity.setQty(qty);
                cartRepo.save(entity);
            }
        }
        return ResponseEntity.ok(cartRepo.findByUsername(username));
    }

    @DeleteMapping("/{username}/items/{id}")
    @Transactional
    public ResponseEntity<List<CartItem>> deleteItem(
            @PathVariable String username,
            @PathVariable("id") Long productId
    ) {
        cartRepo.findByUsernameAndProductId(username, productId)
                .ifPresent(cartRepo::delete);
        return ResponseEntity.ok(cartRepo.findByUsername(username));
    }

    @DeleteMapping("/{username}")
    @Transactional
    public ResponseEntity<List<CartItem>> clearCart(@PathVariable String username) {
        List<CartItem> items = cartRepo.findByUsername(username);
        cartRepo.deleteAll(items);
        return ResponseEntity.ok(cartRepo.findByUsername(username));
    }
}


