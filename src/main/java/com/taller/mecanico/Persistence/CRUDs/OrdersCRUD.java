package com.taller.mecanico.Persistence.CRUDs;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.mecanico.Persistence.Model.Order;
import com.taller.mecanico.Persistence.Model.OrderStatus;
import com.taller.mecanico.Persistence.Model.User;

public interface OrdersCRUD extends JpaRepository<Order, Long> {

    List<Order> findAllByStatusOrderByCreatedAtAsc(OrderStatus status);
    List<Order> findByCustomerId(Long id);

    List<Order> findByMechanic(User mechanic);

}
