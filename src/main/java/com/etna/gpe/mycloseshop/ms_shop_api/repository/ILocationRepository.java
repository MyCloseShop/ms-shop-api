package com.etna.gpe.mycloseshop.ms_shop_api.repository;

import com.etna.gpe.mycloseshop.ms_shop_api.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ILocationRepository extends JpaRepository<Location, UUID> {
}
