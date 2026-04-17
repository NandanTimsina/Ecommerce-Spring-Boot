package com.Project.Ecommerce.controller;

import com.Project.Ecommerce.Payload.CategoryDTO;
import com.Project.Ecommerce.Payload.CategoryResponse;
import com.Project.Ecommerce.config.appConstants;
import com.Project.Ecommerce.model.Category;
import com.Project.Ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<CategoryResponse> getAllCategories
            (@RequestParam(name = "pageNumber",defaultValue = appConstants.PAGE_NUMBER,required = false) int PageNumber,
             @RequestParam(name="pageSize",defaultValue = appConstants.PAGE_SIZE,required = false) int PageSize,
             @RequestParam(name="sortBy",defaultValue = appConstants.SORT_CATEGORIES_BY,required = false) String sortBy,
             @RequestParam(name="sortOrder",defaultValue = appConstants.SORT_DIR,required = false) String sortOrder
             ){

        CategoryResponse categories = categoryService.getAllCategories(PageNumber,PageSize,sortBy,sortOrder);

        return new ResponseEntity<>(categories, HttpStatus.OK);

        //After this ,will go to frontend through Jackson library
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO saved=categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }


    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){

            CategoryDTO status = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status,HttpStatus.OK);
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId){
            CategoryDTO savedCategory = categoryService.updateCategory(categoryDTO, categoryId);

            return new ResponseEntity<>(savedCategory, HttpStatus.OK);
    }
}

