package com.product.service;

import java.util.List;

import com.product.dto.ProductDto;

public interface ProductService {
	public List<ProductDto> getProductList(String labelType);
}
