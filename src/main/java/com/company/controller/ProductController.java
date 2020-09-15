package com.company.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.company.files.UploadFileResponse;
import com.company.model.Product;
import com.company.service.FileStorageService;
import com.company.service.ProductServiceImpl;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/product")
public class ProductController {
	
	private static final  Logger logger = LoggerFactory.getLogger(ProductController.class); 
	
	@Autowired
	ProductServiceImpl productService = new ProductServiceImpl();
	
	@Autowired
	FileStorageService fileStorageService;
	
	@Autowired
	public RestTemplate restTemplate;
	
	
	@GetMapping(value = "" , produces = {MediaType.APPLICATION_JSON_VALUE})
	CollectionModel<Product> getProducts() {
		List<Product> productList = productService.getProducts();
		List<Product> products = new ArrayList<Product>();
		Flux<Product> sequence = Flux.fromIterable(productList);
		sequence.log().subscribe(new Subscriber() {
			
			private Subscription s;
			int count; 

			@Override
			public void onSubscribe(Subscription s) {
				s.request(2);
				this.s = s;
				
			}

			@Override
			public void onNext(Object t) {
				products.add((Product) t);
				count++; 
				if (count == 2) {
					count = 0; 
					logger.info(" Populated Products : "+ products);
					s.request(2);
				} 
				
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
			
			
		}); 
		
		logger.info(products.toString());
		
		return new CollectionModel<>(productList).add(linkTo(methodOn(ProductController.class).getProducts()).withSelfRel());
		
	}
	
	@GetMapping("/{id}")
	public Product getProduct(@PathVariable("id") Long id) throws Exception {
		return productService.getProduct(id); 
	}
	
	@PostMapping(value = "")
	public Map<String, Object> createProducts(@RequestParam(value ="id") Long id,
				@RequestParam(value = "name") String name, @RequestParam(value = "price") Integer price) throws Exception {
		
		productService.createProduct(id, name, price); 
		
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("status", "Product added!");
		return map;
	}
	
	@PutMapping(value = "")
	public Product updateProductUsingJson(@Valid @RequestBody Product product) {
		productService.updateProduct(product);
		return product; 
	}
	
	@DeleteMapping("/{id}")
	public Map<String, Object> deleteProduct(@PathVariable("id") Long id) {
		productService.deleteProduct(id);
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("status", "Product deleted!");
		return map;
	}
	
	// http cache at 30 seconds 
	@GetMapping("/getpricewithcache")
	public ResponseEntity<String> getPriceWithCache(){
		CacheControl cacheControl = CacheControl.maxAge(30,TimeUnit.SECONDS ); 
		int price = (int) Math.random(); 
		
		String testBody = "<h3> Current price:"+ price+" Dirhams </h3>" + "<h3> Response from server received at:"
				+LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "</h3>";
		return ResponseEntity.ok().cacheControl(cacheControl).body(testBody); 	
	}
	
	@PostMapping("/uploadFile")
	public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
		String fileName = fileStorageService.storeFile(file);
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/product/downloadFile/")
				.path(fileName)
				.toUriString(); 
		
		return new UploadFileResponse(fileName, fileDownloadUri,
				file.getContentType(),file.getSize());
		
	}
	
	@PostMapping("/uploadFiles")
	public List<UploadFileResponse> uploadFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.asList(files)
				.stream()
				.map(file -> uploadFile(file))
				.collect(Collectors.toList());
		
	}
	
	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource = fileStorageService.loadFileAsResource(fileName);
		
		String contentType = null; 
		try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch(IOException ex ) {
				System.out.println("Could not determine file type."); 
			}
		
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" +resource.getFilename() + "\"")
				.body(resource);
	}

}
