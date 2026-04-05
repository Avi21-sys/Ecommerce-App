package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.ProductDto;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping
    public List<ProductDto> getAll(){
        return service.getAllProducts();
    }

    @GetMapping("/search")
    public List<ProductDto> search(@RequestParam String keyword){
        return service.searchProduct(keyword);
    }

    @GetMapping("/category/{category}")
    public List<ProductDto> getByCategory(@PathVariable String category){
        return service.getByCategory(category);
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product){
        return service.save(product);
    }

    @GetMapping("/filter")
    public Page<ProductDto> filter(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category){
        return service.filterProducts(page, size, keyword, category);
    }

    @GetMapping("/page")
    public Page<Product> getProductsPage(
            @RequestParam int page,
            @RequestParam int size
    ){
        return service.getProductsPage(page, size);
    }

}
