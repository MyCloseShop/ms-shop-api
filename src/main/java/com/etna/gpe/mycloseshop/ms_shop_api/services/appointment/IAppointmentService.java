package com.etna.gpe.mycloseshop.ms_shop_api.services.appointment;


import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.CreateAppointmentRequest;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


public interface IAppointmentService {
    List<LocalTime> getAvailableSlots(String shopId, LocalDate date, Integer duration);

    List<LocalTime> calculateAvailableSlots(LocalTime startTime, LocalTime endTime, int durationMinutes, List<Appointment> existingAppointments);

    AppointmentDto createAppointment(CreateAppointmentRequest request) throws NoSuchElementException, IllegalArgumentException;

    List<AppointmentDto> getAppointmentsByShopId(UUID shopId);

    List<AppointmentDto> getAppointmentsByClientId(UUID clientId);

    Boolean paidAppointment(UUID appointmentId);

    Boolean confirmAppointment(UUID appointmentId);

    List<AppointmentDto> getAppointmentsByClientIdAndStatus(UUID clientId, String status);

    List<AppointmentDto> getAppointmentsByShopIdAndStatus(UUID shopId, String status);
}
