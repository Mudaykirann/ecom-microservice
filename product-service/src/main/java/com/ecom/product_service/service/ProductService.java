package com.ecom.product_service.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.ecom.product_service.model.Product;
import com.ecom.product_service.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> getAll() {
		return productRepository.findAll();
	}

	public Product getById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
	}

	public Product create(Product product) {
		product.setProductId(null);
		return productRepository.save(product);
	}

	public Product update(Long id, Product product) {
		Product existing = getById(id);
		existing.setProductName(product.getProductName());
		existing.setProductDescription(product.getProductDescription());
		existing.setProductPrice(product.getProductPrice());
		return productRepository.save(existing);
	}

	public void delete(Long id) {
		if (!productRepository.existsById(id)) {
			throw new NoSuchElementException("Product not found with id: " + id);
		}
		productRepository.deleteById(id);
	}
}
