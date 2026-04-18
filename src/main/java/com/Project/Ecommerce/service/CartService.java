package com.Project.Ecommerce.service;

import com.Project.Ecommerce.Payload.CartDTO;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
}
