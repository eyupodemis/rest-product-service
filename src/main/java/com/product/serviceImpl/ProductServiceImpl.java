package com.product.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.product.beans.Product;
import com.product.beans.StaticParams;
import com.product.dto.ProductDto;
import com.product.repository.ProductRepository;
import com.product.service.ProductService;

@Component
public class ProductServiceImpl implements ProductService  {
	@Autowired
	ProductRepository productRepository;
	
	@Override
	public List<ProductDto> getProductList(String labelType){
		List<ProductDto> producDtotList = new ArrayList<>();
		List<Product> productList = new ArrayList<>();
		
		for(Product product : productRepository.getProductsArray()) {
			Product productTemp = calculatePrice(product,labelType);
			if(productTemp.getPriceReduction() > 0) 
				productList.add(productTemp);
		}
		
		Comparator<Product> comparator = ((p1,p2) -> (int)(p1.getPriceReduction() - p2.getPriceReduction()));
		Collections.sort(productList, comparator.reversed());
		
		productList.forEach(product -> {
				ProductDto productDto = new ProductDto();
			    productDto.setProductId(product.getProductId());
			    productDto.setTitle(product.getTitle());
			    productDto.setColorSwatches(product.getColorSwatches());
			    productDto.setNowPrice(product.getNowPrice());
			    productDto.setPriceLabel(product.getPriceLabel());
			    producDtotList.add(productDto);
			});

		return producDtotList;
	}
	
	private Product calculatePrice(Product product, String labelType) {
		
		JSONObject price = new JSONObject(product.getPrice());
    	double priceWas = 0;
    	double then1 = 0;
    	double then2 = 0;
       	double priceFrom = 0;
    	String priceWasString = null;
    	String then1String = null;
    	String then2String = null;
    	String currency = "";
    	String priceToDispay = "";
    	String priceLabel = "";
    	
    	
    	try{	
    		priceFrom = price.getFloat("now");
    		currency = price.getString("currency").equals("GBP") ? "\u00A3" : "";
    		priceToDispay = priceFrom >=10 ? Integer.toString((int)priceFrom) : Double.toString(priceFrom);
    		product.setNowPrice(currency+priceToDispay);	
    	}catch(JSONException e){		
    		priceFrom = price.getJSONObject("now").getDouble("from");
    		priceToDispay = priceFrom >= 10 ? Integer.toString((int)priceFrom) : Double.toString(priceFrom);
    		product.setNowPrice(currency+priceToDispay);
    	}
    	
    	
    	try{
    		priceWas = price.getFloat("was");	
    	}catch(JSONException e){
    		priceWas = 0;
    	}

    	try{
	    	then1 = price.getFloat("then1");
    	}catch(JSONException e){
    		then1 = 0 ;
    	}

    	try{
	    	then2 = price.getFloat("then2");
    	}catch(JSONException e){
    		then2 = 0;
    	}
    	

    	priceWasString = priceWas >= 10 ? Integer.toString((int)priceWas):Double.toString(priceWas);
    	then1String = then1>= 10 ? Integer.toString((int)then1) : Double.toString(then1);
    	then2String = then2 >= 10 ? Integer.toString((int)then2) : Double.toString(then2);


		if(labelType.equals(StaticParams.SHOW_PER_DISCOUNT)){
			int percentage = 0;
			if(priceWas != 0){
    			percentage = (int) ((priceFrom * 100)/ priceWas);    				
			}else{
    			percentage = 100;
			}
			priceLabel = percentage+ "% off - now "+ currency + priceToDispay;
			
		}else if(labelType.equals(StaticParams.SHOW_WAS_THEN_NOW)) {
	    	if(then2 != 0){
	    		priceLabel = "Was "+ currency + priceWasString + ", then "+ currency + then2String + ", now "+ currency + priceToDispay;
	    	}else if(then2 == 0 && then1 != 0){
	    		priceLabel = "Was "+ currency + priceWasString + ", then "+ currency + then1String + ", now "+ currency + priceToDispay;
	    	}else{
	    		priceLabel = "Was "+ currency + priceWasString + ", now "+ currency + priceToDispay;
	    	}
		}else {
			priceLabel = "Was "+ currency + priceWasString + ", now "+ currency + priceToDispay;
		}
		
		product.setPriceLabel(priceLabel);
    	product.setPriceReduction((Double)priceWas - priceFrom);
    	
    	
    	return product;
	}
}
