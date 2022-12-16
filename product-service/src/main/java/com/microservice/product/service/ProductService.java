package com.microservice.product.service;

import com.microservice.product.dto.ProductDto;
import com.microservice.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {
   public String createProduct(ProductDto productDto);

    List<ProductResponse> findAllProducts();
}
