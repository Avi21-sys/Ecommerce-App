package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductDto;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService{

    private final ProductRepository repo;

    public ProductService(ProductRepository repo){
        this.repo = repo;
    }

    public List<ProductDto> getAllProducts(){
        return repo.findAll()
                .stream()
                .map(this::covertToDto)
                .toList();
    }

    public List<ProductDto> searchProduct(String keyword){
        return repo.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::covertToDto)
                .toList();
    }

    public List<ProductDto> getByCategory(String category){
        return repo.findByCategory(category)
                .stream()
                .map(this::covertToDto)
                .toList();
    }

    public Product save(Product product){
        return repo.save(product);
    }

    public Page<ProductDto> filterProducts(int page, int size, String keyword, String category) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<Product> result;

        if (keyword != null && !keyword.isEmpty() && category != null && !category.equals("all")) {
            result= repo.findByNameContainingIgnoreCaseAndCategory(keyword, category, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            result= repo.findByNameContainingIgnoreCase(keyword, pageable);
        } else if (category != null && !category.equals("all")) {
            result= repo.findByCategory(category, pageable);
        } else {
            result =  repo.findAll(pageable);
        }
        return result.map(this::covertToDto);
    }
    // Pagination
    public Page<Product> getProductsPage(int page, int size){
        return  repo.findAll(PageRequest.of(page, size));
    }

    private ProductDto covertToDto(Product p){
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getImage(),
                p.getCategory()
        );
    }
}
