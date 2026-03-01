package com.example.Medicine.Drug_OS.Service;

import com.example.Medicine.Drug_OS.DTO.AddToCartRequest;
import com.example.Medicine.Drug_OS.DTO.CartItemDTO;
import com.example.Medicine.Drug_OS.DTO.CartResponse;
import com.example.Medicine.Drug_OS.Entity.CartItem;
import com.example.Medicine.Drug_OS.Entity.Product;
import com.example.Medicine.Drug_OS.Entity.User;
import com.example.Medicine.Drug_OS.Reposit.CartItemRepository;
import com.example.Medicine.Drug_OS.Reposit.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepo productRepo;
    private final UserService userService;

    @Autowired
    public CartService(CartItemRepository cartItemRepository, ProductRepo productRepo, UserService userService) {
        this.cartItemRepository = cartItemRepository;
        this.productRepo = productRepo;
        this.userService = userService;
    }

    public CartResponse addToCart(AddToCartRequest request) {
        User user = userService.getCurrentAuthenticatedUser();
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if ("OUT_OF_STOCK".equals(product.getProductStatus()) || product.getProductQuantity() < request.getQuantity()) {
            throw new RuntimeException("Product is unavailable or not enough stock.");
        }

        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByUserAndProduct_ProductId(user, product.getProductId());

        if (existingCartItemOpt.isPresent()) {
            CartItem cartItem = existingCartItemOpt.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            if (product.getProductQuantity() < newQuantity) {
                throw new RuntimeException("Not enough stock to add more items.");
            }
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(newCartItem);
        }
        return getCart();
    }

    public CartResponse getCart() {
        User user = userService.getCurrentAuthenticatedUser();
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        return convertToCartResponse(cartItems);
    }

    public CartResponse updateQuantity(Integer productId, Integer quantity) {
        User user = userService.getCurrentAuthenticatedUser();
        if (quantity <= 0) {
            return removeFromCart(productId);
        }
        CartItem cartItem = cartItemRepository.findByUserAndProduct_ProductId(user, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        Product product = cartItem.getProduct();
        if ("OUT_OF_STOCK".equals(product.getProductStatus()) || product.getProductQuantity() < quantity) {
            throw new RuntimeException("Product is unavailable or not enough stock.");
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return getCart();
    }

    public CartResponse removeFromCart(Integer productId) {
        User user = userService.getCurrentAuthenticatedUser();
        CartItem cartItem = cartItemRepository.findByUserAndProduct_ProductId(user, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));
        cartItemRepository.delete(cartItem);
        return getCart();
    }

    public void clearCart() {
        User user = userService.getCurrentAuthenticatedUser();
        cartItemRepository.deleteByUser(user);
    }

    private CartResponse convertToCartResponse(List<CartItem> cartItems) {
        List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        BigDecimal totalPrice = cartItemDTOs.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse response = new CartResponse();
        response.setItems(cartItemDTOs);
        response.setTotalPrice(totalPrice);
        response.setTotalItems(cartItemDTOs.size());
        return response;
    }

    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        Product product = cartItem.getProduct();
        dto.setId(cartItem.getId());
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setUnitPrice(BigDecimal.valueOf(product.getProductPrice()));
        dto.setQuantity(cartItem.getQuantity());
        dto.setSubtotal(BigDecimal.valueOf(product.getProductPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return dto;
    }
}