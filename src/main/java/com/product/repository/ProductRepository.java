package com.product.repository;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.product.beans.ColorSwatch;
import com.product.beans.Product;

@Component
public class ProductRepository {

	List<Product> productsArray;
	
	ProductRepository(){
		this.productsArray = getJsonData();
	}
	
	public List<Product> getJsonData() {

		String productUri = "https://jl-nonprod-syst.apigee.net/v1/categories/600001506/products?key=2ALHCAAs6ikGRBoy6eTHA58RaG097Fma";
		List<Product> productsArray = new ArrayList<Product>();

		String productJson = new RestTemplate().getForObject(productUri, String.class);
		JSONArray products = new JSONObject(productJson).getJSONArray("products");

		products.forEach(product -> {
			Product singleProduct = new Product();
			singleProduct.setProductId(((JSONObject) product).getString("productId"));
			singleProduct.setTitle(((JSONObject) product).getString("title"));
			JSONArray colorSwatchesArray = ((JSONObject) product).getJSONArray("colorSwatches");
			List<ColorSwatch> ColorSwatchArray = new ArrayList<ColorSwatch>();

			colorSwatchesArray.forEach(colorSwatch -> {
				ColorSwatch singleColorSwatch = new ColorSwatch();
				String colorName = ((JSONObject) colorSwatch).getString("color");
				singleColorSwatch.setColor(colorName);

				String basicColor = ((JSONObject) colorSwatch).getString("basicColor");
				Color color;
				try {
					Field field = Color.class.getField(basicColor.toLowerCase());
					color = (Color) field.get(null);
				} catch (Exception e) {
					color = Color.black;
				}

				String hexColour = Integer.toHexString(color.getRGB() & 0xffffff);
				if (hexColour.length() < 6) {
					hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
				}

				singleColorSwatch.setRgbColor("#" + hexColour);

				singleColorSwatch.setSkuId(((JSONObject) colorSwatch).getString("skuId"));

				ColorSwatchArray.add(singleColorSwatch);
			});

			singleProduct.setColorSwatches(ColorSwatchArray);
			singleProduct.setPrice(((JSONObject)product).getJSONObject("price").toString());
			//JSONObject priceJson = ((JSONObject)product).getJSONObject("price");
			productsArray.add(singleProduct);
		});
		return productsArray;
	}

	public List<Product> getProductsArray() {
		return productsArray;
	}
}
