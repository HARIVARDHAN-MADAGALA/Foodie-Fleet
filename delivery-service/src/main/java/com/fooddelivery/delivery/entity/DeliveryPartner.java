package com.fooddelivery.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_partners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    
    private String vehicleNumber;
    
    @Enumerated(EnumType.STRING)
    private PartnerStatus status = PartnerStatus.AVAILABLE;
    
    private Double currentLatitude;
    private Double currentLongitude;
    private Double rating = 0.0;
    private Integer totalDeliveries = 0;
}
