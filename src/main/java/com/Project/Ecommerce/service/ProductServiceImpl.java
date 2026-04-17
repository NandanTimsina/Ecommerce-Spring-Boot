package com.Project.Ecommerce.service;
import com.Project.Ecommerce.Payload.ProductDTO;
import com.Project.Ecommerce.Payload.ProductResponse;
import com.Project.Ecommerce.exceptions.ApiException;
import com.Project.Ecommerce.exceptions.ResourceNotFoundException;
import com.Project.Ecommerce.model.Category;
import com.Project.Ecommerce.model.Product;
import com.Project.Ecommerce.repository.CategoryRepository;
import com.Project.Ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{
    @Value("${project.image}")
    private String path;


    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;
    private FileServiceImpl fileService;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ModelMapper modelMapper, FileServiceImpl fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
    }

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","CategoryId",categoryId));

        boolean isProductAbsent=true;
        List<Product> products=category.getProducts();
        for(Product value: products){
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductAbsent= false;
                break;
            }
        }
        if(isProductAbsent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
            Double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }else {
            throw new ApiException("Product is already Existed");
        }
    }
    @Override
    public ProductResponse getAllProducts(int PageNumber,int PageSize,String sortBy,String sortOrder) {

        Sort sortOrderAndBy=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageRequest= PageRequest.of(PageNumber,PageSize,sortOrderAndBy);
        Page<Product> PagedProducts=productRepository.findAll(pageRequest);
        List<Product> products=PagedProducts.getContent();

       // List<Product> products=productRepository.findAll();
        List<ProductDTO> productDTOS=products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        if(products.isEmpty()){
            throw new ApiException("There is no Products yet");
        }
        ProductResponse productResponse=new ProductResponse();
        productResponse.setTotalElements(PagedProducts.getTotalElements());
        productResponse.setPageSize(PagedProducts.getSize());
        productResponse.setPageNumber(PagedProducts.getNumber());
        productResponse.setTotalPages(PagedProducts.getTotalPages());
        productResponse.setLastPage(PagedProducts.isLast());
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(long categoryId, int PageNumber,
                                                 int PageSize,String sortBy,String sortOrder) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","CategoryId",categoryId));

        Sort sortOrderAndBy=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageRequest= PageRequest.of(PageNumber,PageSize,sortOrderAndBy);
        Page<Product> PagedProducts=productRepository.findByCategoryOrderByPriceAsc(category,pageRequest);
        List<Product> products=PagedProducts.getContent();
        if(products.isEmpty()){
            throw new ApiException(category.getCategoryName()+" Doesnot have any Product.");
        }

        List<ProductDTO> productDTOS=products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setTotalElements(PagedProducts.getTotalElements());
        productResponse.setPageSize(PagedProducts.getSize());
        productResponse.setPageNumber(PagedProducts.getNumber());
        productResponse.setTotalPages(PagedProducts.getTotalPages());
        productResponse.setLastPage(PagedProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword,int PageNumber,int PageSize,String sortBy,String sortOrder) {
        Sort sortOrderAndBy=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageRequest= PageRequest.of(PageNumber,PageSize,sortOrderAndBy);
        Page<Product> PagedProducts=productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageRequest);

        List<Product> products=PagedProducts.getContent();

        List<ProductDTO> productDTOS=products.stream().
                map(product ->modelMapper.map(product,ProductDTO.class) )
                .toList();
if(products.isEmpty()){
    throw new ApiException(" NO matching Product.");
}

        ProductResponse productResponse=new ProductResponse();
        productResponse.setTotalElements(PagedProducts.getTotalElements());
        productResponse.setPageSize(PagedProducts.getSize());
        productResponse.setPageNumber(PagedProducts.getNumber());
        productResponse.setTotalPages(PagedProducts.getTotalPages());
        productResponse.setLastPage(PagedProducts.isLast());
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Optional<Product> existingProduct=productRepository.findById(productId);
        if(existingProduct.isEmpty()){
            throw new ResourceNotFoundException("Product","ProductId",productId);
        }
        Product product=modelMapper.map(productDTO,Product.class);
            Product productInRepo=existingProduct.get();
            productInRepo.setPrice(product.getPrice());
            productInRepo.setProductName(product.getProductName());
            productInRepo.setDescription(product.getDescription());
            productInRepo.setImage(product.getImage());
            productInRepo.setDiscount(product.getDiscount());
            productInRepo.setSpecialPrice((product.getPrice()-
                    (0.01*product.getDiscount())*product.getPrice()));

            productInRepo.setQuantity(product.getQuantity());
            productRepository.save(productInRepo);
        return modelMapper.map(productInRepo, ProductDTO.class);

    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","ProductID",productId));
        productRepository.delete(product);
        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","ProductID",productId));

        // String path="images/"; //relative directory path ,just the path(name) where we gonna store the image in our server
        String fileName=fileService.uploadImage(path,image);//saves the file(image) on disk and returns the unique filename
        product.setImage(fileName);//set the image name in Product entity,(we usse in dto,response..anywhere)
        Product updatedProduct=productRepository.save(product); // saving in db,saves the new image name in db
        return modelMapper.map(updatedProduct, ProductDTO.class);

    }


}
