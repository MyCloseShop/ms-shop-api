package com.etna.gpe.mycloseshop.ms_shop_api.repository;

import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IOpeningHoursRepository extends JpaRepository<OpeningHours, UUID> {
    List<OpeningHours> findByShopId(UUID shopId);
}
