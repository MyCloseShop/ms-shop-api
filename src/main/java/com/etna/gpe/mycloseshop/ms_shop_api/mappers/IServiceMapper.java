package com.etna.gpe.mycloseshop.ms_shop_api.mappers;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.CreatedServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.ServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IServiceMapper {
    @Mapping(target = "shopId", source = "shop.id")
    CreatedServiceDto toCreatedDto(Service entity);

    @Mapping(target = "shopId", source = "shop.id")
    ServiceDto toDto(Service entity);
}
