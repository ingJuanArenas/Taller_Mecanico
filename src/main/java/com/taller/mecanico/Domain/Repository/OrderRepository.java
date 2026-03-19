package com.taller.mecanico.Domain.Repository;

import java.util.List;

import com.taller.mecanico.Domain.DTOs.CreateOrderDTO;
import com.taller.mecanico.Domain.DTOs.OrderResponseDTO;
import com.taller.mecanico.Domain.DTOs.UpdateOrderDTO;

public interface OrderRepository {

    List<OrderResponseDTO> getAllOrders();

    List<OrderResponseDTO> getAvailableOrders();

    OrderResponseDTO getById(Long id);

    List<OrderResponseDTO> getByCustomer(Long customerId);

    OrderResponseDTO create(CreateOrderDTO order);

    OrderResponseDTO update(Long id, UpdateOrderDTO order);

    OrderResponseDTO takeOrder(Long id, Long mechanicId);

    boolean mechanicHasActiveOrder(Long mechanicId);

    void delete(Long id);
}
