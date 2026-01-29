package com.example.zipkintracking.config;

import com.example.zipkintracking.entity.Order;
import com.example.zipkintracking.entity.User;
import com.example.zipkintracking.repository.OrderRepository;
import com.example.zipkintracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Loading sample data into H2 database...");
        
        // Create sample users
        User user1 = new User(null, "John Doe", "john.doe@example.com", "New York");
        User user2 = new User(null, "Jane Smith", "jane.smith@example.com", "Los Angeles");
        User user3 = new User(null, "Bob Johnson", "bob.johnson@example.com", "Chicago");
        User user4 = new User(null, "Alice Williams", "alice.williams@example.com", "Houston");
        
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);
        user4 = userRepository.save(user4);
        
        log.info("Created {} users", userRepository.count());
        
        // Create sample orders
        Order order1 = new Order(null, user1.getId(), "Laptop", 1299.99, LocalDateTime.now().minusDays(5));
        Order order2 = new Order(null, user1.getId(), "Mouse", 29.99, LocalDateTime.now().minusDays(3));
        Order order3 = new Order(null, user2.getId(), "Keyboard", 89.99, LocalDateTime.now().minusDays(2));
        Order order4 = new Order(null, user2.getId(), "Monitor", 399.99, LocalDateTime.now().minusDays(1));
        Order order5 = new Order(null, user3.getId(), "Headphones", 149.99, LocalDateTime.now());
        Order order6 = new Order(null, user4.getId(), "Webcam", 79.99, LocalDateTime.now().minusHours(5));
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
        orderRepository.save(order4);
        orderRepository.save(order5);
        orderRepository.save(order6);
        
        log.info("Created {} orders", orderRepository.count());
        log.info("Sample data loaded successfully!");
    }
}

