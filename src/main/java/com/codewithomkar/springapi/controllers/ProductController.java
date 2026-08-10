package com.codewithomkar.springapi.controllers;

import com.codewithomkar.springapi.dtos.ProductDTO;
import com.codewithomkar.springapi.entities.Product;
import com.codewithomkar.springapi.mappers.ProductMapper;
import com.codewithomkar.springapi.repositories.CategoryRepository;
import com.codewithomkar.springapi.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {


    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping()
    public List<ProductDTO> fetchAllProducts(
            @RequestParam(name = "categoryId", defaultValue = "", required = false) Byte categoryId
    ) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findAllByCategoryId(categoryId);
        } else {
            products = productRepository.findAllWithCategory();
        }
        return products.stream().map(productMapper::toDto).toList();
    }

    @PostMapping()
    public ResponseEntity<ProductDTO> createProduct(
            UriComponentsBuilder uriBuilder,
            @Valid  @RequestBody ProductDTO productDTO
    ) {
        var category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        var productEntity = productMapper.toEntity(productDTO);
        productEntity.setCategory(category);
        productRepository.save(productEntity);

        productDTO.setId(productEntity.getId());

        var uri = uriBuilder.path("/products/{id}").buildAndExpand(productEntity.getId()).toUri();

        return ResponseEntity.created(uri).body(productMapper.toDto(productEntity));
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO
    ){
        var category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        var fetchedProductEntity = productRepository.findById(id).orElse(null);
        if (fetchedProductEntity == null) {
            return ResponseEntity.notFound().build();
        }

        productMapper.updateProduct(productDTO, fetchedProductEntity);
        fetchedProductEntity.setCategory(category);
        productRepository.save(fetchedProductEntity);
        return ResponseEntity.ok().body(productMapper.toDto(fetchedProductEntity));
    }

    @DeleteMapping({"{id}"})
    public ResponseEntity<ProductDTO> deleteProduct(
            @PathVariable Long id
    ){
        var fetchedProductEntity = productRepository.findById(id).orElse(null);
        if (fetchedProductEntity == null) {
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(fetchedProductEntity);
        return ResponseEntity.noContent().build();
    }
}
