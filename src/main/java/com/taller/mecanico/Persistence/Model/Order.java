package com.taller.mecanico.Persistence.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //

    @Column(name = "customer_id", nullable = false)
    private Long customer; //
   
    @ManyToOne
    @JoinColumn(name = "advisor_id", nullable = false)
    private User advisor;
   
    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private User mechanic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "vehicle_plate", nullable = false)
    private String vehiclePlate;

    @Column(name = "vehicle_model", nullable = false)
    private String vehicleModel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
}
