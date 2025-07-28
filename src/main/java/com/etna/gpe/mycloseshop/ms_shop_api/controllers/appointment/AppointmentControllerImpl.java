package com.etna.gpe.mycloseshop.ms_shop_api.controllers.appointment;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.CreateAppointmentRequest;
import com.etna.gpe.mycloseshop.ms_shop_api.services.appointment.IAppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
public class AppointmentControllerImpl implements IAppointmentController{

    private final IAppointmentService appointmentService;

    public AppointmentControllerImpl(IAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Override
    public ResponseEntity<List<LocalTime>> getAvailableSlots(String shopId, Integer duration, LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(shopId, date, duration));
    }

    @Override
    public ResponseEntity<AppointmentDto> bookAppointment(CreateAppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(request));
    }

    @Override
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByShopId(UUID shopId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByShopId(shopId));
    }

    @Override
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByClientId(UUID clientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByClientId(clientId));
    }

    @Override
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByClientIdAndStatus(UUID clientId, String status) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByClientIdAndStatus(clientId, status));
    }

    @Override
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByShopIdAndStatus(UUID shopId, String status) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByShopIdAndStatus(shopId, status));
    }

    @Override
    public ResponseEntity<Boolean> paidAppointment(UUID appointmentId) {
        Boolean result = appointmentService.paidAppointment(appointmentId);
        return ResponseEntity.ok(result);
    }
}
