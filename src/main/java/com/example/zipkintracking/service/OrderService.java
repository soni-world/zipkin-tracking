package com.example.zipkintracking.service;

import com.example.zipkintracking.entity.Order;
import com.example.zipkintracking.entity.User;
import com.example.zipkintracking.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserService userService;
    
    public List<Order> getAllOrders() {
        log.info("Fetching all orders from database");
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);
        return orderRepository.findById(id);
    }
    
    public Map<String, Object> getOrderWithUserDetails(Long orderId) {
        log.info("Fetching order with user details for order id: {}", orderId);
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            log.warn("Order not found with id: {}", orderId);
            return null;
        }
        
        Order order = orderOpt.get();
        
        // This call to userService will create a span in the trace
        Optional<User> userOpt = userService.getUserById(order.getUserId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("user", userOpt.orElse(null));
        
        log.info("Successfully fetched order and user details");
        return result;
    }
    
    public Order createOrder(Order order) {
        log.info("Creating new order for user id: {}", order.getUserId());
        
        // Validate user exists - this creates a span in the trace
        Optional<User> user = userService.getUserById(order.getUserId());
        if (user.isEmpty()) {
            log.error("Cannot create order - User not found with id: {}", order.getUserId());
            throw new RuntimeException("User not found with id: " + order.getUserId());
        }
        
        log.info("User validated, creating order");
        return orderRepository.save(order);
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        log.info("Fetching orders for user id: {}", userId);
        
        // Validate user exists - this creates a span in the trace
        userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return orderRepository.findByUserId(userId);
    }
    
    public boolean deleteOrder(Long id) {
        log.info("Deleting order with id: {}", id);
        return orderRepository.findById(id)
                .map(order -> {
                    orderRepository.delete(order);
                    return true;
                })
                .orElse(false);
    }
}

