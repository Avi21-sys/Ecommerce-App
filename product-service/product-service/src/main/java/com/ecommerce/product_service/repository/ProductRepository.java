package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategory(String category);

    Page<Product> findByNameContainingIgnoreCaseAndCategory(
            String name, String category, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(
            String name, Pageable pageable);

    Page<Product> findByCategory(
            String category, Pageable pageable);
}
