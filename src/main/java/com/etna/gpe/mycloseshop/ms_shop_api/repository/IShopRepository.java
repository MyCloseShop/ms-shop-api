package com.etna.gpe.mycloseshop.ms_shop_api.repository;

import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface IShopRepository extends PagingAndSortingRepository<Shop, UUID>, JpaRepository<Shop, UUID> {
}
