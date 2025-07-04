package com.etna.gpe.mycloseshop.ms_shop_api.mappers;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface IAppointmentMapper {

    @Mapping(target = "shop.shopId", source = "shop.id")
    @Mapping(target = "shop.ownerId", source = "shop.userId")
    @Mapping(target = "shop.locationId", source = "shop.location.id")
    @Mapping(target = "shop.openingHoursIds", source = "shop.openingHours")
    @Mapping(target = "serviceId", ignore = true)
    @Mapping(target = "clientId", source = "userId")
    @Mapping(target = "date", source = "appointmentDate")
    AppointmentDto toDto(Appointment appointment);

    @AfterMapping
    default void mapServiceId(@MappingTarget AppointmentDto.AppointmentDtoBuilder builder, Appointment appointment) {
        UUID serviceId;
        if (appointment.getService() != null && appointment.getService().getId() != null) {
            serviceId = appointment.getService().getId();
        } else {
            serviceId = appointment.getQuoteId();
        }
        builder.serviceId(serviceId);
    }

    default List<UUID> mapOpeningHoursToIds(List<OpeningHours> openingHours) {
        if (openingHours == null) {
            return Collections.emptyList();
        }
        return openingHours.stream()
                .map(OpeningHours::getId)
                .toList();
    }

}
