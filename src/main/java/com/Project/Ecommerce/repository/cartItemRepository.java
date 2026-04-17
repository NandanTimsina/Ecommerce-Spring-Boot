package com.Project.Ecommerce.repository;

import com.Project.Ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface cartItemRepository extends JpaRepository<CartItem,Long> {
}
