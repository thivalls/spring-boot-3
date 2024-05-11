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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
        List<ProductModel> productList = productRepository.findAll();

        if(!productList.isEmpty()) {
            for (ProductModel product : productList) {
                UUID id = product.getId();
                product.add(linkTo(methodOn(ProductController.class).findById(id)).withSelfRel());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(productList);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> findById(@PathVariable(value="id") UUID id) {
        Optional<ProductModel> product = productRepository.findById(id);

        if(product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        product.get().add(linkTo(methodOn(ProductController.class).findAll()).withRel("Product List"));

        return ResponseEntity.status(HttpStatus.OK).body(product.get());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateById(@PathVariable(value="id") UUID id, @RequestBody @Valid ProductUpdateRequest productUpdateRequest) {
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
