package com.taller.mecanico.Web.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taller.mecanico.Domain.DTOs.UpdateOrderDTO;
import com.taller.mecanico.Domain.Service.OrderService;
import com.taller.mecanico.Persistence.Model.Order;
import com.taller.mecanico.Persistence.Model.OrderStatus;
import com.taller.mecanico.Persistence.Projections.OrderSummary;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;

    

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }



    @GetMapping
    public ResponseEntity<List<OrderSummary>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderSummary> getById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/search/customer")
    public ResponseEntity<List<OrderSummary>> getByCustomer(@RequestParam("customer") Long customerId){
        return ResponseEntity.ok(orderService.getByCustomer(customerId));
    }

    @GetMapping("/search/advisor")
    public ResponseEntity<List<OrderSummary>> getByAdvisor(@RequestParam("advisor") Long advisorId){
        return ResponseEntity.ok(orderService.getByAdvisor(advisorId));
    }

    @GetMapping("/search/mechanic")
    public ResponseEntity<List<OrderSummary>> getByMechanic(@RequestParam("mechanic") Long mechanicId){
        return ResponseEntity.ok(orderService.getByMechanic(mechanicId));
    }

    @GetMapping("/search/status")
    public ResponseEntity<List<OrderSummary>> getByStatus(@RequestParam("status") OrderStatus status){
        return ResponseEntity.ok(orderService.getByStatus(status));
    }

    @GetMapping("/search/date")
    public ResponseEntity<List<OrderSummary>> getByDateRange(@RequestParam("start") String start, @RequestParam("end") String end){
        return ResponseEntity.ok(orderService.getByDateRange(start, end));
    }
    
   @PostMapping
    public ResponseEntity<OrderSummary> create(@RequestBody Order order){
        return ResponseEntity.ok(orderService.create(order));
    }
    
 
    @PutMapping("/update/{id}")
    public ResponseEntity<OrderSummary> update(@PathVariable Long id, @RequestBody UpdateOrderDTO order){
        return ResponseEntity.ok(orderService.update(id, order));
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
