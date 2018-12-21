package com.product.controller;

import java.util.List;

import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.product.beans.StaticParams;
import com.product.dto.ProductDto;
import com.product.serviceImpl.ProductServiceImpl;

@RestController
public class ProductListController {

	
	@Autowired
	ProductServiceImpl productService;
	
	@GetMapping(value="/products")
	@ResponseBody
	public ResponseEntity<List<ProductDto>> getAllProducts(@QueryParam("labelType") String labelType) throws Exception{
		if(labelType == null)
	    	labelType = StaticParams.SHOW_WAS_NOW;
		else if(!(labelType.equals(StaticParams.SHOW_PER_DISCOUNT) ||
				  labelType.equals(StaticParams.SHOW_WAS_NOW) ||
				  labelType.equals(StaticParams.SHOW_WAS_THEN_NOW)) )
			throw new Exception("Wrong type of Label : " + labelType);
        
		return ResponseEntity.ok().body(productService.getProductList(labelType));

	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> exceptionHandler(Exception ex) {
		return new ResponseEntity<String>("An Error Occured." + ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
}