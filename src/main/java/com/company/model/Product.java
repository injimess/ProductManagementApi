package com.company.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.company.Annotation.InRangeCustomAnnotation;
import com.company.controller.ProductController;

import org.springframework.hateoas.RepresentationModel;  

public class Product extends RepresentationModel<Product> {
	
	private Long productID; 
	@Valid
	@NotBlank(message = "Name is required")
	private String productName;
	@Valid
	@NotNull(message ="Price should be specified")
	@InRangeCustomAnnotation(min=1,max=100)
	private Integer productPrice;
	

	public Product(Long productID, String productName, Integer productPrice) {
		super();
		this.productID = productID;
		this.productName = productName;
		this.productPrice = productPrice;	
		
		add(linkTo(methodOn(ProductController.class).deleteProduct(this.productID)).withRel("delete"));
		add(linkTo(methodOn(ProductController.class).updateProductUsingJson(this)).withRel("update"));
		// add(linkTo(methodOn(ProductController.class).getProduct(this.productID)).withSelfRel());
	}
	
	/**
	 * @return the productID
	 */
	public Long getProductID() {
		return productID;
	}

	/**
	 * @param productID the productID to set
	 */
	public void setProductID(Long productID) {
		this.productID = productID;
	}

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the productPrice
	 */
	public Integer getProductPrice() {
		return productPrice;
	}

	/**
	 * @param productPrice the productPrice to set
	 */
	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}

 

}
