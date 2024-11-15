package com.etna.gpe.mycloseshop.ms_shop_api.repository;

import com.etna.gpe.mycloseshop.ms_shop_api.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findAllByShopId(UUID shopId);
}
