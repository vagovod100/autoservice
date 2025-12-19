package com.autoservice.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column(name = "car_id", nullable = false)  
    private Integer carId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    // Добавляем поле для назначения сотрудника
    @Column(name = "assigned_employee", nullable = false)
    private String assignedEmployee = "Nobody";  // По умолчанию "Nobody"

    public Order() {
    }

    public Order(Integer id, Integer clientId, Integer carId, String status,
                 LocalDateTime createdAt, LocalDateTime finishedAt, BigDecimal totalCost, String assignedEmployee) {
        this.id = id;
        this.clientId = clientId;
        this.carId = carId;
        this.status = status;
        this.createdAt = createdAt;
        this.finishedAt = finishedAt;
        this.totalCost = totalCost;
        this.assignedEmployee = assignedEmployee;  // Инициализация сотрудника
    }

    // Getter и Setter для нового поля
    public String getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(String assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    // Оставшиеся геттеры и сеттеры
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}

