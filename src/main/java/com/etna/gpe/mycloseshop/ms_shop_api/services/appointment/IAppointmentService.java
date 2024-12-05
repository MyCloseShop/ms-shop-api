package com.etna.gpe.mycloseshop.ms_shop_api.services.appointment;


import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentResponse;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.CreateAppointmentRequest;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


public interface IAppointmentService {
    List<LocalTime> getAvailableSlots(String shopId, LocalDate date, String serviceId);

    List<LocalTime> calculateAvailableSlots(LocalTime startTime, LocalTime endTime, int durationMinutes, List<Appointment> existingAppointments);

    AppointmentResponse createAppointment(CreateAppointmentRequest request);

    List<AppointmentDto> getAppointmentsByShopId(UUID shopId);

    List<AppointmentDto> getAppointmentsByClientId(UUID clientId);

    Boolean confirmAppointment(UUID appointmentId);

    List<AppointmentDto> getAppointmentsByClientIdAndStatus(UUID clientId, String status);

    List<AppointmentDto> getAppointmentsByShopIdAndStatus(UUID shopId, String status);
}
