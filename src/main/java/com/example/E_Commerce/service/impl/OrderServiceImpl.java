package com.example.E_Commerce.service.impl;

import com.example.E_Commerce.dto.address.AddressResponse;
import com.example.E_Commerce.dto.order.*;
import com.example.E_Commerce.exception.*;
import com.example.E_Commerce.model.*;
import com.example.E_Commerce.repository.*;
import com.example.E_Commerce.security.CustomUserDetails;
import com.example.E_Commerce.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(CustomUserDetails user, OrderRequest request) {
        User buyer = user.getUser();

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException(
                        "Address not found with id: " + request.getAddressId()));

        if (!address.getBuyer().getId().equals(buyer.getId())) {
            throw new AccessDeniedException("You do not have permission to use this address");
        }

        Cart cart = cartRepository.findByUserId(buyer.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user"));

        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(ci -> request.getCartItemIds().contains(ci.getId()))
                .toList();
        if (selectedItems.size() != request.getCartItemIds().size()) {
            throw new CartItemNotFoundException("One or more cart items not found in your cart");
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setBuyer(buyer);
        order.setShippingAddress(address);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : selectedItems) {
            Product product = ci.getProduct();

            if (product.getStock() < ci.getQuantity()) {
                throw new InvalidOrderStateException(
                        "Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - ci.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(ci.getQuantity());
            item.setPrice(product.getPrice());
            item.setOrder(order);
            orderItems.add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        order.setOrderItems(orderItems);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    @Override
    public List<OrderResponse> getMyOrders(CustomUserDetails user) {
        return orderRepository.findByBuyerId(user.getUser().getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Long id, CustomUserDetails user) {
        return mapToResponse(getOwnedOrder(id, user));
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                    "Cannot change status of a " + order.getStatus() + " order");
        }

        order.setStatus(request.getStatus());
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public PaymentResponse payForOrder(Long id, PaymentRequest request, CustomUserDetails user) {
        Order order = getOwnedOrder(id, user);

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new InvalidOrderStateException("Order has already been paid for");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot pay for a cancelled order");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(request.getTransactionId() != null
                ? request.getTransactionId()
                : "TXN-" + UUID.randomUUID());

        if (request.getPaymentMethod() == PaymentMethod.COD) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
            order.setPaymentStatus(PaymentStatus.PENDING);
        } else {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.CONFIRMED);
        }

        order.setPayment(payment);
        Payment savedPayment = paymentRepository.save(payment);
        orderRepository.save(order);

        return mapToPaymentResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long id, CustomUserDetails user) {
        Order order = getOwnedOrder(id, user);
        Payment payment = order.getPayment();

        if (payment == null) {
            throw new PaymentNotFoundException("No payment found for order id: " + id);
        }
        return mapToPaymentResponse(payment);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private Order getOwnedOrder(Long id, CustomUserDetails user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getBuyer().getId().equals(user.getUser().getId())) {
            throw new AccessDeniedException("You do not have permission to access this order");
        }
        return order;
    }

    private OrderResponse mapToResponse(Order saved) {
        OrderResponse response = new OrderResponse();
        HttpServletResponse order;
        response.setOrderId(saved.getOrderId());
        response.setOrderNumber(saved.getOrderNumber());
        response.setOrderDate(saved.getOrderDate());
        response.setStatus(saved.getStatus());
        response.setTotalAmount(saved.getTotalAmount());
        response.setPaymentStatus(saved.getPaymentStatus());
        response.setBuyerId(saved.getBuyer().getId());
        response.setShippingAddress(mapAddress(saved.getShippingAddress()));
        response.setItems(saved.getOrderItems().stream().map(this::mapItem).toList());
        return response;
    }

    private AddressResponse mapAddress(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setStreet(address.getStreet());
        response.setPhone(address.getPhone());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setZipcode(address.getZipCode());
        response.setCountry(address.getCountry());
        response.setIsDefault(address.getIsDefault());
        response.setBuyerId(address.getBuyer().getId());
        return response;
    }

    private OrderItemResponse mapItem(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        return response;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setTransactionId(payment.getTransactionId());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setOrderId(payment.getOrder().getOrderId());
        return response;
    }
}
