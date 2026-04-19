package com.Project.Ecommerce.service;

import com.Project.Ecommerce.Payload.CartDTO;
import com.Project.Ecommerce.Payload.ProductDTO;
import com.Project.Ecommerce.exceptions.ApiException;
import com.Project.Ecommerce.exceptions.ResourceNotFoundException;
import com.Project.Ecommerce.model.Cart;
import com.Project.Ecommerce.model.CartItem;
import com.Project.Ecommerce.model.Product;
import com.Project.Ecommerce.repository.CartItemRepository;
import com.Project.Ecommerce.repository.CartRepository;
import com.Project.Ecommerce.repository.ProductRepository;
import com.Project.Ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    //Steps for below
    //Find Exitsing cart or create one
    //Retrive Product Details
    //Perform Validations
    //Create Cart Item
    //Save Cart Item
    //Return updated cart

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
      Cart cart=createCart();

        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product ","ProductId ",productId));

       CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);

        if(cartItem != null){
            throw new ApiException("Product "+product.getProductName()+" already exists ");
        }
        if(product.getQuantity()==0){
            throw new ApiException(product.getProductName()+" is not Available");
        }
        if(product.getQuantity()< quantity){
            throw new ApiException("Please make an order of the "+product.getProductName()+
                    " less then or equal to the quantity "+ product.getQuantity()+" .");
        }
        CartItem newCartItem=new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);
        cart.getCartItems().add(newCartItem);


        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice()+(product
                .getSpecialPrice()*quantity));

        cartRepository.save(cart);
        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);

        List<CartItem> cartItems=cart.getCartItems();
        Stream<ProductDTO> productStream=cartItems.stream().map(
                item ->{
                    ProductDTO map=modelMapper.map(item.getProduct(),ProductDTO.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                });
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }


    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts=cartRepository.findAll();

        if(carts.isEmpty()){
            throw new ApiException("No cart exists");
        }
        List<CartDTO> cartDTOS=carts.stream()
                .map(cart ->
                        {
                            CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
                            List<ProductDTO> products=cart.getCartItems().stream()
                                    .map(p-> modelMapper.map(p.getProduct(),ProductDTO.class))
                                    .toList();
                            cartDTO.setProducts(products);
                            return cartDTO;
                        }).toList();
        return cartDTOS;
    }


    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart=cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart ","CartId ",cartId);
        }
        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c-> c.getProduct().setQuantity(c.getQuantity()));

        List<ProductDTO> products=cart.getCartItems().stream()
                .map(p-> modelMapper.map(p.getProduct(),ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }


    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId).orElseThrow(() ->
                new ResourceNotFoundException("Cart", "CartId ", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product ", "ProductId ", productId));

        if (product.getQuantity() == 0) {
            throw new ApiException(product.getProductName() + " is not Available");
        }

        if (product.getQuantity() < quantity) {
            throw new ApiException("Please make an order of the " + product.getProductName() +
                    " less then or equal to the quantity " + product.getQuantity() + " .");
        }
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new ApiException("Product " + product.getProductName() + " not Available in the cart ");
        }

        int newQuantity=cartItem.getQuantity()+quantity;


        if ( newQuantity< 0) {
            throw new ApiException("The result quantity cannot be negative.");
        }

        if ( newQuantity==0) {
            deleteProductFromCart(cartId,productId);
        } else{
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItem.setDiscount(product.getDiscount());
        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
        cartRepository.save(cart);
    }
            CartItem updatedItem=cartItemRepository.save(cartItem);

            if(updatedItem.getQuantity()==0){
                cartItemRepository.deleteById(updatedItem.getCartItemId());
            }

            CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
            List<CartItem> cartItems=cart.getCartItems();

            Stream<ProductDTO> productStream=cartItems.stream().map(item-> {
                ProductDTO prd=modelMapper.map(item.getProduct(),ProductDTO.class);
                prd.setQuantity(item.getQuantity());
                return prd;
                    });
            cartDTO.setProducts(productStream.toList());
            return cartDTO;
        }


    @Override
    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart=cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart "," Cart ",cartId));

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem==null){
            throw new ResourceNotFoundException("Product ","ProductId ",productId);
        }
        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()* cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product " +cartItem.getProduct().getProductName() +" removed from the cart.";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() ->
                new ResourceNotFoundException("Cart", "CartId ", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product ", "ProductId ", productId));

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem==null){
            throw new ApiException("Product "+product.getProductName()+" not available in the cart!");
        }
        double cartPrice =cart.getTotalPrice()-
                (cartItem.getProductPrice()*cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice+ (cartItem.getProductPrice()*cartItem.getQuantity()));
        cartItem=cartItemRepository.save(cartItem);
    }


    private Cart createCart(){

        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return  userCart;
        }
        Cart cart =new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart=cartRepository.save(cart);

        return newCart;
    }

}
