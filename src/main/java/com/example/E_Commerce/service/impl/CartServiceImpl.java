package com.example.E_Commerce.service.impl;

import com.example.E_Commerce.dto.cart.CartItemRequest;
import com.example.E_Commerce.dto.cart.CartItemResponse;
import com.example.E_Commerce.dto.cart.CartResponse;
import com.example.E_Commerce.exception.CartItemNotFoundException;
import com.example.E_Commerce.exception.CartNotFoundException;
import com.example.E_Commerce.exception.ProductNotFoundException;
import com.example.E_Commerce.model.Cart;
import com.example.E_Commerce.model.CartItem;
import com.example.E_Commerce.model.Product;
import com.example.E_Commerce.repository.CartRepository;
import com.example.E_Commerce.repository.ProductRepository;
import com.example.E_Commerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    @Override
    public CartResponse getCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse addItem(Long id, CartItemRequest request) {
        Cart cart = getCartByUserId(id);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + request.getProductId()));
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getCartItems().add(newItem);
            newItem.setPrice(product.getPrice());
        }

        recalculateTotal(cart);
        Cart saved = cartRepository.save(cart);
        return mapToResponse(saved);
    }

    @Override
    public CartResponse updateItemQuantity(Long id, Long cartItemId, CartItemRequest request) {
        Cart cart = getCartByUserId(id);

        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(
                        "Cart item not found with id: " + cartItemId));

        item.setQuantity(request.getQuantity());

        recalculateTotal(cart);
        Cart saved = cartRepository.save(cart);
        return mapToResponse(saved);
    }

    @Override
    public CartResponse removeItemFromCart(Long id, Long cartItemId) {
        Cart cart = getCartByUserId(id);

        boolean removed = cart.getCartItems()
                .removeIf(item -> item.getId().equals(cartItemId));

        if (!removed) {
            throw new CartItemNotFoundException("Cart item not found with id: " + cartItemId);
        }

        recalculateTotal(cart);
        Cart saved = cartRepository.save(cart);
        return mapToResponse(saved);
    }

    @Override
    public void clearCart(Long id) {
        Cart cart = getCartByUserId(id);
        cart.getCartItems().clear();
        recalculateTotal(cart);
        cartRepository.save(cart);
    }

    private Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + userId));
    }

    private void recalculateTotal(Cart cart) {
    BigDecimal total = cart.getCartItems().stream()
            .map(item -> item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    cart.setTotalPrice(total);
}

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> response = cart.getCartItems().stream()
                .map(item -> {
                    BigDecimal unitPrice = item.getProduct().getPrice();
                    BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new CartItemResponse(
                            item.getId(),
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            unitPrice,
                            item.getQuantity(),
                            subTotal
                    );
                }).toList();

        return new CartResponse(cart.getId(), response, cart.getTotalPrice());
    }
}
