package com.Project.Ecommerce.service;

import com.Project.Ecommerce.Payload.CategoryDTO;
import com.Project.Ecommerce.Payload.CategoryResponse;
import com.Project.Ecommerce.exceptions.ApiException;
import com.Project.Ecommerce.exceptions.ResourceNotFoundException;
import com.Project.Ecommerce.model.Category;
import com.Project.Ecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
   private final CategoryRepository categoryRepository;

   @Autowired
   private ModelMapper modelMapper;


    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse getAllCategories(int pageNumber,int pageSize,String sortBy,String sortOrder) {

        Sort sortOrderAndBy=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageRequest= PageRequest.of(pageNumber,pageSize,sortOrderAndBy);
        Page<Category> categories=categoryRepository.findAll(pageRequest);
        List<Category> all=categories.getContent();

        if(all.isEmpty()){
            throw new ApiException("No Category Exists.");
        }
        List<CategoryDTO> categoryDTOS=all.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setTotalElements(categories.getTotalElements());
        categoryResponse.setPageSize(categories.getSize());
        categoryResponse.setPageNumber(categories.getNumber());
        categoryResponse.setTotalPages(categories.getTotalPages());
        categoryResponse.setLastPage(categories.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        Category existed = categoryRepository
                .findByCategoryName(categoryDTO.getCategoryName());

        if (existed != null) {
            throw new ApiException(
                    "Category with name " + categoryDTO.getCategoryName() + " already exists."
            );
        }
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

        @Override
        public CategoryDTO deleteCategory(Long categoryId) {
           Optional<Category> optionalCategory=categoryRepository.findById(categoryId);
           Category savedCAtegory=optionalCategory
                   .orElseThrow(()->new ResourceNotFoundException("Category","CategoryID",categoryId));
           categoryRepository.delete(savedCAtegory);
           return modelMapper.map(optionalCategory, CategoryDTO.class);
        }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category SendedDTO =modelMapper.map(categoryDTO, Category.class);
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","CategoryId",categoryId));

        existingCategory.setCategoryName(SendedDTO.getCategoryName());
        existingCategory.setCategoryId(SendedDTO.getCategoryId());
        categoryRepository.save(existingCategory);

        return modelMapper.map(existingCategory, CategoryDTO.class);



    }
}
