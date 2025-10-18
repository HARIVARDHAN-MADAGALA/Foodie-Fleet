package com.fooddelivery.delivery.repository;

import com.fooddelivery.delivery.entity.DeliveryPartner;
import com.fooddelivery.delivery.entity.PartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    List<DeliveryPartner> findByStatus(PartnerStatus status);
}
