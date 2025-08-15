package com.ecommerce.product.config;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.repo.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedProducts(ProductRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                Product p1 = new Product(); p1.setName("Dumbbells (Pair)"); p1.setPrice(1499.00);
                Product p2 = new Product(); p2.setName("Kettlebell 16kg"); p2.setPrice(2199.00);
                Product p3 = new Product(); p3.setName("Yoga Mat"); p3.setPrice(799.00);
                Product p4 = new Product(); p4.setName("Resistance Bands Set"); p4.setPrice(599.00);
                Product p5 = new Product(); p5.setName("Gym Gloves"); p5.setPrice(349.00);
                Product p6 = new Product(); p6.setName("Skipping Rope"); p6.setPrice(199.00);
                Product p7 = new Product(); p7.setName("Foam Roller"); p7.setPrice(999.00);
                Product p8 = new Product(); p8.setName("Adjustable Bench"); p8.setPrice(6999.00);
                Product p9 = new Product(); p9.setName("Barbell 20kg"); p9.setPrice(4999.00);
                Product p10 = new Product(); p10.setName("Weight Plates 10kg (Pair)"); p10.setPrice(2599.00);
                repo.save(p1); repo.save(p2); repo.save(p3); repo.save(p4); repo.save(p5);
                repo.save(p6); repo.save(p7); repo.save(p8); repo.save(p9); repo.save(p10);
            }
        };
    }
}


