package com.Project.Ecommerce.service;
import com.Project.Ecommerce.Payload.CategoryDTO;
import com.Project.Ecommerce.Payload.CategoryResponse;
import com.Project.Ecommerce.model.Category;
import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories(int PageNumber,int PageSize,String sortBy,String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}