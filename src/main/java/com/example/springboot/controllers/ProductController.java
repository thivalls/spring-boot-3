package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductCreateRequest;
import com.example.springboot.dtos.ProductUpdateRequest;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> create(@RequestBody @Valid ProductCreateRequest productCreateRequest) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productCreateRequest, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.findAll());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> findById(@PathVariable(value="id") UUID id) {
        Optional<ProductModel> product = productRepository.findById(id);

        if(product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(product.get());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> findById(@PathVariable(value="id") UUID id, @RequestBody @Valid ProductUpdateRequest productUpdateRequest) {
        Optional<ProductModel> product = productRepository.findById(id);

        if(product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        var productModel = product.get();
        BeanUtils.copyProperties(productUpdateRequest, productModel);

        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value="id") UUID id) {
        Optional<ProductModel> product = productRepository.findById(id);

        if(product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        // productRepository.deleteById(id);
        productRepository.delete(product.get());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
}
