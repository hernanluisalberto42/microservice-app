package com.microservice.product.service;

import com.microservice.product.dto.ProductDto;
import com.microservice.product.dto.ProductResponse;
import com.microservice.product.model.Product;
import com.microservice.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImp implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public String createProduct(ProductDto productDto) {
        Product product = productRepository.save(convertToProduct(productDto));
        log.info("Product {} is saved", product.getIdProduct());
        return "Product save successful";
    }

    @Override
    public List<ProductResponse> findAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductResponse convertToDto(Product product) {
        return ProductResponse
                .builder()
                .idProduct(product.getIdProduct())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    private Product convertToProduct(ProductDto productDto){
        Product product=new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        return product;
    }
}
