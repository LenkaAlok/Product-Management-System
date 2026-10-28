package com.jforce.productmanagement.service;
import com.jforce.productmanagement.entity.Category;
import com.jforce.productmanagement.entity.Product;
import com.jforce.productmanagement.repository.CategoryRepository;
import com.jforce.productmanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with ID: " + id));
    }

    public Product updateProduct(Long id, Product updatedProduct) {

        Product existingProduct = getProductById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setCategory(updatedProduct.getCategory());

        return productRepository.save(existingProduct);
    }

    public Product updateProductStatus(Long id, boolean enabled) {
        Product product = getProductById(id);
        product.setEnabled(enabled);
        return productRepository.save(product);
    }
    // Save a new product in the database
    public Product createProduct(Product product) {
        if (product.getCategory() == null ||
                product.getCategory().getId() == null) {

            throw new RuntimeException("Category ID is required");
        }

        // Find the category in the database
        Category category = categoryRepository
                .findById(product.getCategory().getId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        // Connect the existing category to the product
        product.setCategory(category);

        // Save the product in MySQL and return it
        return productRepository.save(product);
    }
}

