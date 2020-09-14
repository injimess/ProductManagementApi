package com.company.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.model.Product;

@Service
public class ProductServiceImpl implements ProductService{

	List<Product> products;
	public ProductServiceImpl() {
		products = new ArrayList<Product>();
		products.add(new Product(1l,"iphone",1999));
		products.add(new Product(2l,"speaker",599));
		products.add(new Product(3l,"book",99));
	}
	
	
    @Override
	public List<Product> getProducts() {
		System.out.println(products.toString()) ; 
		return products;
	}

	@Override
	public Product getProduct(Long id) {
		Iterator<Product> iterator = products.iterator();
		while (iterator.hasNext()) {
			Product product = iterator.next();
			if (product.getProductID().equals(id)) {
				return product;
			}
		}
		
		return null;
	}

	@Override
	public void createProduct(Long productId, String name, Integer price) throws Exception {
		// TODO Auto-generated method stub
		products.add(new Product(productId, name, price)); 		
	}

	@Override
	public void updateProduct(Product product) {
		// TODO Auto-generated method stub
		
		getProduct(product.getProductID()).setProductPrice(product.getProductPrice());
		getProduct(product.getProductID()).setProductName(product.getProductName());
		
	}

	@Override
	public void deleteProduct(Long id) {
		// TODO Auto-generated method stub
		System.out.println("Status.."+products.remove(getProduct(id)));
		
	}

		
}
