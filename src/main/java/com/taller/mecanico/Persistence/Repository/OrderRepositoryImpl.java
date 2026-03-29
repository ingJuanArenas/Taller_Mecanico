package com.taller.mecanico.Persistence.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.taller.mecanico.Domain.DTOs.CreateOrderDTO;
import com.taller.mecanico.Domain.DTOs.OrderResponseDTO;
import com.taller.mecanico.Domain.DTOs.UpdateOrderDTO;
import com.taller.mecanico.Domain.Exceptions.NotFoundException;
import com.taller.mecanico.Domain.DTOs.InterveneOrderDTO;
import com.taller.mecanico.Domain.Exceptions.BadRequestException;
import com.taller.mecanico.Domain.Repository.OrderRepository;
import com.taller.mecanico.Persistence.CRUDs.OrdersCRUD;
import com.taller.mecanico.Persistence.CRUDs.ProceduresCRUD;
import com.taller.mecanico.Persistence.CRUDs.UsersCRUD;
import com.taller.mecanico.Persistence.Mapper.OrderMapper;
import com.taller.mecanico.Persistence.Model.OrderStatus;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    private final OrdersCRUD ordersCRUD;
    private final OrderMapper orderMapper;
    private final UsersCRUD usersCRUD;
    private final ProceduresCRUD proceduresCRUD;

    
    public OrderRepositoryImpl(OrdersCRUD ordersCRUD, OrderMapper orderMapper, UsersCRUD usersCRUD, ProceduresCRUD proceduresCRUD) {
        this.ordersCRUD = ordersCRUD;
        this.orderMapper = orderMapper;
        this.usersCRUD = usersCRUD;
        this.proceduresCRUD = proceduresCRUD;
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
        if (Boolean.TRUE.equals(order.complete()) && existingOrder.getStatus() == OrderStatus.IN_PROGRESS) {
            existingOrder.setStatus(OrderStatus.COMPLETED);
            var procs = proceduresCRUD.findByOrderId(id);
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            for(var p : procs) {
                if (p.getPrice() != null) total = total.add(p.getPrice());
            }
            existingOrder.setTotal(total);
        }
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
            .anyMatch(order -> order.getStatus() == OrderStatus.IN_PROGRESS && !Boolean.TRUE.equals(order.getMechanicFinished()));

        return hasActiveOrder;
        
    }

    @Override
    public OrderResponseDTO releaseOrder(Long id, Long mechanicId) {
        var existingOrder = ordersCRUD.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        if (!existingOrder.getMechanic().getId().equals(mechanicId)) throw new BadRequestException("Order does not belong to mechanic");
        if (existingOrder.getStatus() != OrderStatus.IN_PROGRESS) throw new BadRequestException("Order is not in progress");
        var procs = proceduresCRUD.findByOrderId(id);
        if (procs.isEmpty()) throw new BadRequestException("Order must have at least one procedure");
        existingOrder.setMechanicFinished(true);
        var saved = ordersCRUD.save(existingOrder);
        return orderMapper.toDTO(saved);
    }

    @Override
    public OrderResponseDTO interveneOrder(Long id, InterveneOrderDTO dto) {
        var existingOrder = ordersCRUD.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        if (existingOrder.getStatus() == OrderStatus.COMPLETED) throw new BadRequestException("Cannot intervene a completed order");
        existingOrder.setInterventionReason(dto.reason());
        existingOrder.setStatus(dto.status());
        if (dto.status() == OrderStatus.COMPLETED) {
             existingOrder.setMechanicFinished(true);
        }
        var saved = ordersCRUD.save(existingOrder);
        return orderMapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {

        var existingOrder = ordersCRUD.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        ordersCRUD.delete(existingOrder);
    }


    
}
