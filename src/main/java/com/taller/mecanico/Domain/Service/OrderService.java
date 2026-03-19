package com.taller.mecanico.Domain.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taller.mecanico.Domain.DTOs.CreateOrderDTO;
import com.taller.mecanico.Domain.DTOs.OrderResponseDTO;
import com.taller.mecanico.Domain.DTOs.UpdateOrderDTO;
import com.taller.mecanico.Domain.Exceptions.BadRequestException;
import com.taller.mecanico.Domain.Exceptions.NotFoundException;
import com.taller.mecanico.Persistence.Model.OrderStatus;
import com.taller.mecanico.Persistence.Model.UserRole;
import com.taller.mecanico.Persistence.Repository.OrderRepositoryImpl;
import com.taller.mecanico.Persistence.Repository.UserRepositoryImpl;

@Service
public class OrderService {
    
    private final OrderRepositoryImpl orderRepository;
    private final UserRepositoryImpl userRepository;

    
    

    public OrderService(OrderRepositoryImpl orderRepository, UserRepositoryImpl userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<OrderResponseDTO> getAll(){
        var orders = orderRepository.getAllOrders();
        if (orders.isEmpty()) {
            throw new NotFoundException("No content found");
        }
        return orders;
    }

    public List<OrderResponseDTO> getAvailableOrders(){
        var orders =  orderRepository.getAvailableOrders();
        if (orders.isEmpty()) {
            throw new NotFoundException("No content found");
        }

        return orders;
    }

    public List<OrderResponseDTO> getByCustomer(Long customerId){
        var orders= orderRepository.getByCustomer(customerId);
        if (orders.isEmpty()) {
            throw new NotFoundException("No content found");
        }
        return orders;
    }

    public OrderResponseDTO getById(Long id){
        return orderRepository.getById(id);
    }

    public boolean mechanicHasActiveOrder(Long mechanicId){
        var role = userRepository.getById(mechanicId).role().equals(UserRole.MECHANIC);
        if (!role) throw new BadRequestException("Only mechanics can take orders");
        return orderRepository.mechanicHasActiveOrder(mechanicId);
    }

    public OrderResponseDTO create(CreateOrderDTO order){
        return orderRepository.create(order);
    }

    public OrderResponseDTO update(Long id ,UpdateOrderDTO order){
        var orderToUpdate= orderRepository.getById(id);
        if (orderToUpdate.status().equals(OrderStatus.CREATED)) throw new BadRequestException("Cannot update a Order in CREATED status");
        return orderRepository.update(id, order);
    }

    public OrderResponseDTO takeOrder(Long id, Long mechanicId){
        var role = userRepository.getById(mechanicId).role().equals(UserRole.MECHANIC);
        if (!role) throw new BadRequestException("Only mechanics can take orders");
        if (orderRepository.mechanicHasActiveOrder(mechanicId)) throw new BadRequestException("Mechanic has active order");
        var orderStatusCreated = orderRepository.getById(id).status().equals(OrderStatus.CREATED);
        if (!orderStatusCreated) throw new BadRequestException("Order is not available to be taken");

        return orderRepository.takeOrder(id, mechanicId);
    }

    public void delete(Long id){
        var order = orderRepository.getById(id).status().equals(OrderStatus.CREATED);
        if (!order) throw new BadRequestException("Only delete orders in CREATED status");
        orderRepository.delete(id);
    }



}
