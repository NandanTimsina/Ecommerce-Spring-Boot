package com.Project.Ecommerce.controller;

import com.Project.Ecommerce.Payload.ProductDTO;
import com.Project.Ecommerce.Payload.ProductResponse;
import com.Project.Ecommerce.config.appConstants;
import com.Project.Ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")

public class ProductController {

    ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("admin/categories/{CategoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                  @PathVariable Long CategoryId){
        ProductDTO iproductDTO=productService.addProduct(CategoryId,productDTO);
        return new ResponseEntity<>(iproductDTO, HttpStatus.CREATED);
    }
    @GetMapping("public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name = "pageNumber",defaultValue = appConstants.PAGE_NUMBER,required = false) int PageNumber,
                                                          @RequestParam(name="pageSize",defaultValue = appConstants.PAGE_SIZE,required = false) int PageSize,
                                                          @RequestParam(name="sortBy",defaultValue = appConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
                                                          @RequestParam(name="sortOrder",defaultValue = appConstants.SORT_DIR,required = false) String sortOrder){
        ProductResponse productResponse=productService.getAllProducts(PageNumber,PageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @GetMapping("public/categories/{CategoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable long CategoryId,
                                                                 @RequestParam(name = "pageNumber",defaultValue = appConstants.PAGE_NUMBER,required = false) int PageNumber,
                                                                 @RequestParam(name="pageSize",defaultValue = appConstants.PAGE_SIZE,required = false) int PageSize,
                                                                 @RequestParam(name="sortBy",defaultValue = appConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
                                                                 @RequestParam(name="sortOrder",defaultValue = appConstants.SORT_DIR,required = false) String sortOrder){
        ProductResponse productResponse=productService.getProductsByCategory(CategoryId,PageNumber,PageSize,sortBy,sortOrder);
        return  new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @GetMapping("public/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(name = "pageNumber",defaultValue = appConstants.PAGE_NUMBER,required = false) int PageNumber,
                                                                @RequestParam(name="pageSize",defaultValue = appConstants.PAGE_SIZE,required = false) int PageSize,
                                                                @RequestParam(name="sortBy",defaultValue = appConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
                                                                @RequestParam(name="sortOrder",defaultValue = appConstants.SORT_DIR,required = false) String sortOrder){
        ProductResponse productResponse=productService.getProductsByKeyword(keyword,PageNumber,PageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

    @PutMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId){
        ProductDTO iproductDTO=productService.updateProduct(productDTO,productId);
        return new ResponseEntity<>(productDTO,HttpStatus.OK);

    }
    @DeleteMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO productDTO=productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO,HttpStatus.OK);
    }
    @PutMapping("/product/{ProductId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long ProductId,
                                                         @RequestParam("image")MultipartFile image) throws IOException {
        ProductDTO productDTO=productService.updateProductImage(ProductId,image);
        return new ResponseEntity<>(productDTO,HttpStatus.OK);
    }
}
