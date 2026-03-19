package com.taller.mecanico.Persistence.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.taller.mecanico.Domain.DTOs.CreateOrderDTO;
import com.taller.mecanico.Domain.DTOs.OrderResponseDTO;
import com.taller.mecanico.Domain.DTOs.UpdateOrderDTO;
import com.taller.mecanico.Domain.Exceptions.NotFoundException;
import com.taller.mecanico.Domain.Repository.OrderRepository;
import com.taller.mecanico.Persistence.CRUDs.OrdersCRUD;
import com.taller.mecanico.Persistence.CRUDs.UsersCRUD;
import com.taller.mecanico.Persistence.Mapper.OrderMapper;
import com.taller.mecanico.Persistence.Model.OrderStatus;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    private final OrdersCRUD ordersCRUD;
    private final OrderMapper orderMapper;
    private final UsersCRUD usersCRUD;

    
    public OrderRepositoryImpl(OrdersCRUD ordersCRUD, OrderMapper orderMapper, UsersCRUD usersCRUD) {
        this.ordersCRUD = ordersCRUD;
        this.orderMapper = orderMapper;
        this.usersCRUD = usersCRUD;
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        var orders = ordersCRUD.findAll();
        return orderMapper.toDTOs(orders);
    }

    @Override
    public List<OrderResponseDTO> getAvailableOrders() {
        var orders = ordersCRUD.findAllByStatusOrderByCreatedAtAsc(OrderStatus.CREATED);
        return orderMapper.toDTOs(orders);
    }

     @Override
    public List<OrderResponseDTO> getByCustomer(Long customerId) {
       var orders = ordersCRUD.findByCustomerId(customerId);
        return  orderMapper.toDTOs(orders);
    }

    @Override
    public OrderResponseDTO getById(Long id) {
        var order = ordersCRUD.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        return orderMapper.toDTO(order);
    }

    @Override
    public OrderResponseDTO create(CreateOrderDTO order) {

        var orderEntity = orderMapper.toEntity(order);
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setStatus(OrderStatus.CREATED);

        var advisor = usersCRUD.findById(order.advisorId()).orElseThrow(()->  new NotFoundException("Facturador no encontrado"));
        orderEntity.setAdvisor(advisor);

        var savedOrder = ordersCRUD.save(orderEntity);
        return  orderMapper.toDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO update(Long id, UpdateOrderDTO order) {
        var existingOrder = ordersCRUD.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        orderMapper.updateEntityFromDTO(order, existingOrder);
        var updatedOrder = ordersCRUD.save(existingOrder);
        return orderMapper.toDTO(updatedOrder);
    }


    @Override
    public OrderResponseDTO takeOrder(Long id, Long mechanicId) {
        var existingOrder = ordersCRUD.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        var mechanic = usersCRUD.findById(mechanicId)
            .orElseThrow(() -> new NotFoundException("Mechanic not found"));

        existingOrder.setMechanic(mechanic);
        existingOrder.setStatus(OrderStatus.IN_PROGRESS);
        ordersCRUD.save(existingOrder);

        return orderMapper.toDTO(existingOrder);
    }

    @Override
    public boolean mechanicHasActiveOrder(Long mechanicId) {
        var mechanic = usersCRUD.findById(mechanicId)
            .orElseThrow(() -> new NotFoundException("Mechanic not found"));
        var mechanicOrders = ordersCRUD.findByMechanic(mechanic);

        boolean hasActiveOrder = mechanicOrders.stream()
            .anyMatch(order -> order.getStatus() == OrderStatus.IN_PROGRESS);

        return hasActiveOrder;
        
    }

    @Override
    public void delete(Long id) {

        var existingOrder = ordersCRUD.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        ordersCRUD.delete(existingOrder);
    }


    
}
