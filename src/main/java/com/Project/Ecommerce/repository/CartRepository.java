package com.Project.Ecommerce.repository;

import com.Project.Ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart,Long> {
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.user.email=?1")
    Cart findCartByEmail(String email);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.user.email=?1 AND c.cartId=?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);
}
