package com.taller.mecanico.Domain.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.taller.mecanico.Domain.DTOs.UpdateOrderDTO;
import com.taller.mecanico.Persistence.Model.Order;
import com.taller.mecanico.Persistence.Model.OrderStatus;
import com.taller.mecanico.Persistence.Projections.OrderSummary;
import com.taller.mecanico.Persistence.Repository.OrderRepositoryImpl;

@Service
public class OrderService {
    
    private final OrderRepositoryImpl orderRepository;

        public OrderService(OrderRepositoryImpl orderRepository) {
        this.orderRepository = orderRepository;
    }

    
    public List<OrderSummary> getAll(){
        return orderRepository.getAll();
    }

    public OrderSummary getById(Long id){
        return orderRepository.getById(id);
    }

    public List<OrderSummary> getByCustomer(Long customerId){
        return orderRepository.getByCustomer(customerId);
    }

    public List<OrderSummary> getByAdvisor(Long advisorId){
        return orderRepository.getByAdvisor(advisorId);
    }

    //bymechanic
    public List<OrderSummary> getByMechanic(Long mechanicId){
        return orderRepository.getByMechanic(mechanicId);
    }

    //bystatus
    public List<OrderSummary> getByStatus(OrderStatus status){
        return orderRepository.getByStatus(status);
    }


    //bydaterange
    public List<OrderSummary> getByDateRange(String start, String end){
       LocalDate startDate = LocalDate.parse(start);
       LocalDate endDate =LocalDate.parse(end);

        return orderRepository.getByDateRange(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    public OrderSummary create(Order order){
        return orderRepository.create(order);
    }

    public OrderSummary update(Long id ,UpdateOrderDTO order){
        return orderRepository.update(id, order);
    }

    //delete
    public void delete(Long id){
        orderRepository.delete(id);
    }



}
