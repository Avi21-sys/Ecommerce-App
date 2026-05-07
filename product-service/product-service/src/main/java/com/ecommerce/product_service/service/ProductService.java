package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductDto;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.InvalidProductDataException;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<ProductDto> getAllProducts() {
        logger.debug("Fetching all products from repository");
        List<ProductDto> products = repo.findAll().stream().map(this::convertToDTO).toList();
        logger.info("Successfully retrieved {} products", products.size());
        return products;
    }

    public List<ProductDto> searchProduct(String keyword) {
        logger.debug("Searching for products with keyword: {}", keyword);
        List<ProductDto> results = repo.findByNameContainingIgnoreCase(keyword)
                .stream().map(this::convertToDTO).toList();
        logger.info("Search completed. Found {} products for keyword: {}", results.size(), keyword);
        return results;
    }

    public List<ProductDto> getByCategory(String category) {
        logger.debug("Fetching products for category: {}", category);
        List<ProductDto> products = repo.findByCategory(category)
                .stream().map(this::convertToDTO).toList();
        logger.info("Successfully retrieved {} products for category: {}", products.size(), category);
        return products;
    }

    public ProductDto getProduct(Long id) {
        logger.debug("Fetching product with id: {}", id);
        Product product = repo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with id: {}", id);
                    return new ProductNotFoundException("Product not found with id: " + id);
                });
        logger.info("Successfully retrieved product with id: {}", id);
        return convertToDTO(product);
    }

    public ProductDto createProduct(ProductDto dto) {
        logger.debug("Creating new product with name: {}, price: {}, category: {}", 
                   dto.getName(), dto.getPrice(), dto.getCategory());
        
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            logger.warn("Product creation failed: Product name is required");
            throw new InvalidProductDataException("Product name is required");
        }
        if (dto.getPrice() <= 0) {
            logger.warn("Product creation failed: Product price must be positive, provided: {}", dto.getPrice());
            throw new InvalidProductDataException("Product price must be positive");
        }
        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            logger.warn("Product creation failed: Product category is required");
            throw new InvalidProductDataException("Product category is required");
        }
        
        Product product = new Product(null, dto.getName(), dto.getPrice(), dto.getImage(), dto.getCategory());
        Product savedProduct = repo.save(product);
        logger.info("Product created successfully with id: {}, name: {}", savedProduct.getId(), savedProduct.getName());
        return convertToDTO(savedProduct);
    }

    public ProductDto updateProduct(Long id, ProductDto dto) {
        logger.debug("Updating product with id: {}", id);

        Product product = repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidProductDataException("Product name is required");
        }
        if (dto.getPrice() <= 0) {
            throw new InvalidProductDataException("Product price must be positive");
        }
        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            throw new InvalidProductDataException("Product category is required");
        }

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setImage(dto.getImage());
        product.setCategory(dto.getCategory());

        Product savedProduct = repo.save(product);
        logger.info("Product updated successfully with id: {}", savedProduct.getId());
        return convertToDTO(savedProduct);
    }

    public void deleteProduct(Long id) {
        logger.debug("Deleting product with id: {}", id);
        Product product = repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        repo.delete(product);
        logger.info("Product deleted successfully with id: {}", id);
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

        logger.debug("Filtering products - page: {}, size: {}, keyword: {}, category: {}, sortBy: {}, sortDir: {}", 
                    page, size, keyword, category, sortBy, sortDir);

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
            logger.debug("Filtering by both keyword and category");
            result = repo.findByNameContainingIgnoreCaseAndCategory(keyword, category, pageable);
        } else if (hasKeyword) {
            logger.debug("Filtering by keyword only");
            result = repo.findByNameContainingIgnoreCase(keyword, pageable);
        } else if (hasCategory) {
            logger.debug("Filtering by category only");
            result = repo.findByCategory(category, pageable);
        } else {
            logger.debug("No filters applied, fetching all products with pagination");
            result = repo.findAll(pageable);
        }

        Page<ProductDto> dtoResult = result.map(this::convertToDTO);
        logger.info("Filter completed. Found {} total products, {} on current page", 
                   result.getTotalElements(), result.getNumberOfElements());
        return dtoResult;
    }

    public Page<Product> getProductsPage(int page, int size) {
        logger.debug("Fetching products page - page: {}, size: {}", page, size);
        Page<Product> result = repo.findAll(PageRequest.of(page, size));
        logger.info("Retrieved {} products on page {}, total pages: {}", 
                   result.getNumberOfElements(), page, result.getTotalPages());
        return result;
    }

    private ProductDto convertToDTO(Product p) {
        return new ProductDto(p.getId(), p.getName(), p.getPrice(), p.getImage(), p.getCategory());
    }
}
