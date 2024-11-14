package com.etna.gpe.mycloseshop.ms_shop_api.mappers;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.opening_hours.OpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IOpeningHoursMapper {
    @Mapping(target = "shopId", source = "shop.id")
    OpeningHoursDto toDto(OpeningHours entity);

    List<OpeningHours> toListDto(List<OpeningHours> entities);
}
