package com.Project.Ecommerce.repository;

import com.Project.Ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface cartRepository extends JpaRepository<Cart,Long> {
}
