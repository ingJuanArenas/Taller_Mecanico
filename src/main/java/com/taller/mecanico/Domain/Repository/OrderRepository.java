package com.taller.mecanico.Domain.Repository;

import java.time.LocalDateTime;
import java.util.List;

import com.taller.mecanico.Domain.DTOs.UpdateOrderDTO;
import com.taller.mecanico.Persistence.Model.OrderStatus;
import com.taller.mecanico.Persistence.Projections.OrderSummary;
import com.taller.mecanico.Persistence.Model.Order;

public interface OrderRepository {
    // interface for getAll, getById, bycustomer, byadvisor, create, update, delete
    //revisar lo de las fechas
    List<OrderSummary> getAll();
    OrderSummary getById(Long id);
    List<OrderSummary> getByCustomer(Long customerId);
    List<OrderSummary> getByAdvisor(Long advisorId);
    List<OrderSummary> getByMechanic(Long mechanicId);
    List<OrderSummary> getByStatus(OrderStatus status);
    List<OrderSummary> getByDateRange(LocalDateTime start, LocalDateTime end);
    OrderSummary create(Order order);
    OrderSummary update(Long id, UpdateOrderDTO order);
    void delete(Long id);
}
