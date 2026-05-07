package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.ProductDto;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping
    public List<ProductDto> getAll(){
        logger.info("Request received: Get all products");
        List<ProductDto> products = service.getAllProducts();
        logger.info("Successfully retrieved {} products", products.size());
        return products;
    }

    @GetMapping("/search")
    public List<ProductDto> search(@RequestParam String keyword){
        logger.info("Request received: Search products with keyword: {}", keyword);
        List<ProductDto> results = service.searchProduct(keyword);
        logger.info("Search completed. Found {} products matching keyword: {}", results.size(), keyword);
        return results;
    }

    @GetMapping("/category/{category}")
    public List<ProductDto> getByCategory(@PathVariable String category){
        logger.info("Request received: Get products by category: {}", category);
        List<ProductDto> products = service.getByCategory(category);
        logger.info("Successfully retrieved {} products for category: {}", products.size(), category);
        return products;
    }

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable Long id){
        logger.info("Request received: Get product with id: {}", id);
        ProductDto product = service.getProduct(id);
        logger.info("Successfully retrieved product with id: {}", id);
        return product;
    }

    @PostMapping
    public ProductDto addProduct(@Valid @RequestBody ProductDto dto){
        logger.info("Request received: Create product with name: {}, price: {}, category: {}", 
                   dto.getName(), dto.getPrice(), dto.getCategory());
        ProductDto createdProduct = service.createProduct(dto);
        logger.info("Product created successfully with id: {}", createdProduct.getId());
        return createdProduct;
    }

    @PutMapping("/{id}")
    public ProductDto updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        logger.info("Request received: Update product with id: {}", id);
        ProductDto updatedProduct = service.updateProduct(id, dto);
        logger.info("Product updated successfully with id: {}", updatedProduct.getId());
        return updatedProduct;
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        logger.info("Request received: Delete product with id: {}", id);
        service.deleteProduct(id);
        logger.info("Product deleted successfully with id: {}", id);
    }

    @GetMapping("/filter")
    public Page<ProductDto> filter(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir){
        logger.info("Request received: Filter products - page: {}, size: {}, keyword: {}, category: {}, sortBy: {}, sortDir: {}", 
                   page, size, keyword, category, sortBy, sortDir);
        Page<ProductDto> results = service.filterProducts(page, size, keyword, category, sortBy, sortDir);
        logger.info("Filter completed. Found {} products on page {}", results.getNumberOfElements(), page);
        return results;
    }

    @GetMapping("/page")
    public Page<Product> getProductsPage(
            @RequestParam int page,
            @RequestParam int size
    ){
        logger.info("Request received: Get products page - page: {}, size: {}", page, size);
        Page<Product> results = service.getProductsPage(page, size);
        logger.info("Retrieved {} products on page {}", results.getNumberOfElements(), page);
        return results;
    }

}
