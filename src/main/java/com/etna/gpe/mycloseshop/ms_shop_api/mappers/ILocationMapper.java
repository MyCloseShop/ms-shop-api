package com.etna.gpe.mycloseshop.ms_shop_api.mappers;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.location.LocationDto;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ILocationMapper {
    @Mapping(target = "shopId", source = "shop.id")
    LocationDto toDto(Location entity);
}
