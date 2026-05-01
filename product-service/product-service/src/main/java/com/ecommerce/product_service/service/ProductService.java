package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductDto;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.InvalidProductDataException;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<ProductDto> getAllProducts() {
        return repo.findAll().stream().map(this::convertToDTO).toList();
    }

    public List<ProductDto> searchProduct(String keyword) {
        return repo.findByNameContainingIgnoreCase(keyword)
                .stream().map(this::convertToDTO).toList();
    }

    public List<ProductDto> getByCategory(String category) {
        return repo.findByCategory(category)
                .stream().map(this::convertToDTO).toList();
    }

    public ProductDto getProduct(Long id) {
        Product product = repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    public ProductDto createProduct(ProductDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidProductDataException("Product name is required");
        }
        if (dto.getPrice() <= 0) {
            throw new InvalidProductDataException("Product price must be positive");
        }
        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            throw new InvalidProductDataException("Product category is required");
        }
        Product product = new Product(null, dto.getName(), dto.getPrice(), dto.getImage(), dto.getCategory());
        return convertToDTO(repo.save(product));
    }

    /**
     * Filter products with optional keyword, category, and server-side sorting.
     *
     * @param sortBy  field name to sort by (e.g. "price"). Null/empty = no sort.
     * @param sortDir "desc" for descending, anything else = ascending.
     */
    public Page<ProductDto> filterProducts(int page, int size,
                                           String keyword, String category,
                                           String sortBy, String sortDir) {

        Sort sort = (sortBy != null && !sortBy.isEmpty())
                ? ("desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending())
                : Sort.unsorted();

        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Product> result;

        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasCategory = category != null && !category.equals("all");

        if (hasKeyword && hasCategory) {
            result = repo.findByNameContainingIgnoreCaseAndCategory(keyword, category, pageable);
        } else if (hasKeyword) {
            result = repo.findByNameContainingIgnoreCase(keyword, pageable);
        } else if (hasCategory) {
            result = repo.findByCategory(category, pageable);
        } else {
            result = repo.findAll(pageable);
        }

        return result.map(this::convertToDTO);
    }

    public Page<Product> getProductsPage(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    private ProductDto convertToDTO(Product p) {
        return new ProductDto(p.getId(), p.getName(), p.getPrice(), p.getImage(), p.getCategory());
    }
}