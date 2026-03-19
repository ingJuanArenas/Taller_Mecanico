package com.taller.mecanico.Web.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taller.mecanico.Domain.DTOs.CreateOrderDTO;
import com.taller.mecanico.Domain.DTOs.OrderResponseDTO;
import com.taller.mecanico.Domain.DTOs.UpdateOrderDTO;
import com.taller.mecanico.Domain.Service.OrderService;

import jakarta.validation.Valid;

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
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getById( @PathVariable Long id){
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/search/customer")
    public ResponseEntity<List<OrderResponseDTO>> getByCustomer( @RequestParam("customer") Long customerId){
        return ResponseEntity.ok(orderService.getByCustomer(customerId));
    }

    @GetMapping("/active/{mechanicId}")
    public ResponseEntity<Boolean> mechanicHasActiveOrder( @PathVariable Long mechanicId) {
        return ResponseEntity.ok(orderService.mechanicHasActiveOrder(mechanicId));
    }

    
   @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody CreateOrderDTO order){
        return ResponseEntity.ok(orderService.create(order));
    }
    
 
    @PutMapping("/update/{id}")
    public ResponseEntity<OrderResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateOrderDTO order){
        return ResponseEntity.ok(orderService.update(id, order));
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete( @PathVariable Long id){
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<OrderResponseDTO>> getAvailableOrders() {
        return ResponseEntity.ok(orderService.getAvailableOrders());
    }

    @PutMapping("/take/{id}")
    public ResponseEntity<OrderResponseDTO> takeOrder(
         @PathVariable Long id,
         @RequestParam("mechanicId") Long mechanicId) {
        return ResponseEntity.ok(orderService.takeOrder(id, mechanicId));
    }

    

}
