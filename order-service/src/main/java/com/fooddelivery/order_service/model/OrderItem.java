package com.fooddelivery.order_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // saved only  the ID of the menu item, not the whole object, 
    // because the actual food details live in the Restaurant Service!
    private Long menuItemId;
    
    private String name; // Storing the name at the time of purchase in case the restaurant changes it later
    private Integer quantity;
    private Double price;
}