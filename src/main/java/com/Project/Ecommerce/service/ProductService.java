package com.Project.Ecommerce.service;
import com.Project.Ecommerce.Payload.ProductDTO;
import com.Project.Ecommerce.Payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getProductsByCategory(long categoryId,int PageNumber,int PageSize,String sortBy,String sortOrder);

    ProductResponse getProductsByKeyword(String keyword,int PageNumber,int PageSize,String sortBy,String sortOrder);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder);
}
