package com.etna.gpe.mycloseshop.ms_shop_api.mappers;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface IAppointmentMapper {

    @Mapping(target = "shop.shopId", source = "shop.id")
    @Mapping(target = "shop.ownerId", source = "shop.userId")
    @Mapping(target = "shop.locationId", source = "shop.location.id")
    @Mapping(target = "shop.openingHoursIds", source = "shop.openingHours")
    @Mapping(target = "service.shopId", source = "service.shop.id")
    @Mapping(target = "clientId", source = "userId")
    @Mapping(target = "date", source = "appointmentDate")
    AppointmentDto toDto(Appointment appointment);

    default List<UUID> mapOpeningHoursToIds(List<OpeningHours> openingHours) {
        if (openingHours == null) {
            return Collections.emptyList();
        }
        return openingHours.stream()
                .map(OpeningHours::getId)
                .toList();
    }

}
