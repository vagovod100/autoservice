package com.autoservice.service;

import com.autoservice.entity.Order;
import com.autoservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final ClientService clientService;
    private final CarService carService;  // Добавляем сервис для работы с автомобилями

    public OrderService(OrderRepository repository, ClientService clientService, CarService carService) {
        this.repository = repository;
        this.clientService = clientService;
        this.carService = carService;
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Optional<Order> getOrderById(Integer id) {
        return repository.findById(id);
    }

    public Order createSimpleOrder(Integer clientId, String status, BigDecimal totalCost, Integer carId) {
        // Проверяем, что клиент существует
        clientService.getClientById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Клиент с id=" + clientId + " не найден"));

        // Проверяем, что автомобиль существует
        carService.getCarById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Автомобиль с id=" + carId + " не найден"));

        Order order = new Order();
        order.setClientId(clientId);
        order.setCarId(carId);  // Устанавливаем carId
        order.setStatus(status);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalCost(totalCost);

        return repository.save(order);
    }
}

