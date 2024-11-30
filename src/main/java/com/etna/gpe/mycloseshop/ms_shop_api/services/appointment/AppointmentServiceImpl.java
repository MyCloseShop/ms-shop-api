package com.etna.gpe.mycloseshop.ms_shop_api.services.appointment;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentResponse;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.CreateAppointmentRequest;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.AppointmentStatus;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Service;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import com.etna.gpe.mycloseshop.ms_shop_api.mappers.IAppointmentMapper;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IAppointmentRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IServiceRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IShopRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@org.springframework.stereotype.Service
public class AppointmentServiceImpl implements IAppointmentService {

    public static final String SHOP_NOT_FOUND = "Shop not found";
    private final IAppointmentRepository appointmentRepository;
    private final IShopRepository shopRepository;
    private final IServiceRepository serviceRepository;
    private final IAppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(
            IAppointmentRepository appointmentRepository,
            IShopRepository shopRepository,
            IServiceRepository serviceRepository,
            IAppointmentMapper appointmentMapper
    ) {
        this.appointmentRepository = appointmentRepository;
        this.shopRepository = shopRepository;
        this.serviceRepository = serviceRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public List<LocalTime> getAvailableSlots(String shopId, LocalDate date, String serviceId) {

        Service service = serviceRepository.findById(UUID.fromString(serviceId))
                .orElseThrow(() -> new NoSuchElementException("Service not found"));

        int durationMinutes = service.getDuration();


        Shop shop = shopRepository.findById(UUID.fromString(shopId))
                .orElseThrow(() -> new NoSuchElementException(SHOP_NOT_FOUND));

        OpeningHours openingHours = shop.getOpeningHours()
                .stream()
                .filter(oh -> oh.getDayOfWeek().name().equals(date.getDayOfWeek().name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Shop is closed on this day"));

        LocalTime startTime = openingHours.getStartTime();
        LocalTime endTime = openingHours.getEndTime();

        List<Appointment> existingAppointments = appointmentRepository.findByShopAndAppointmentDateAndStatusNot(
                shop, date, AppointmentStatus.CANCELED
        );


        return calculateAvailableSlots(startTime, endTime, durationMinutes, existingAppointments);
    }

    @Override
    public List<LocalTime> calculateAvailableSlots(LocalTime shopStartTime, LocalTime shopEndTime, int durationMinutes, List<Appointment> existingAppointments) {
        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime currentTime = shopStartTime;

        while (currentTime.plusMinutes(durationMinutes).isBefore(shopEndTime) || currentTime.plusMinutes(durationMinutes).equals(shopEndTime)) {
            LocalTime finalCurrentTime = currentTime;
            boolean isAvailable = existingAppointments.stream().noneMatch(appointment ->
                    // Vérifier si le créneau demandé chevauche un rendez-vous existant
                    finalCurrentTime.isBefore(appointment.getEndTime()) && finalCurrentTime.plusMinutes(durationMinutes).isAfter(appointment.getStartTime())
            );

            if (isAvailable) {
                availableSlots.add(currentTime);
            }

            currentTime = currentTime.plusMinutes(durationMinutes);
        }

        return availableSlots;
    }

    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new NoSuchElementException(SHOP_NOT_FOUND));
        Service service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new NoSuchElementException("Service not found"));

        LocalTime endTime = request.startTime().plusMinutes(service.getDuration());

        boolean isSlotAvailable = isSlotAvailable(shop, request.date(), request.startTime(), endTime);
        if (!isSlotAvailable) {
            throw new IllegalArgumentException("The requested slot is already booked");
        }

        Appointment appointment = new Appointment();
        appointment.setShop(shop);
        appointment.setService(service);
        appointment.setUserId(request.clientId());
        appointment.setAppointmentDate(request.date());
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.PENDING);

        appointment = appointmentRepository.save(appointment);

        // TODO: Send rabbitmq message to notify the shop owner

        return new AppointmentResponse(
                appointment.getId(),
                shop.getId(),
                service.getId(),
                appointment.getUserId(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus()
        );
    }

    @Override
    public List<AppointmentDto> getAppointmentsByShopId(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NoSuchElementException(SHOP_NOT_FOUND));

        List<Appointment> appointmentList = appointmentRepository.findByShop(shop);

        return appointmentList.stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByClientId(UUID clientId) {
        List<Appointment> appointmentList = appointmentRepository.findByUserId(clientId);

        return appointmentList.stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    private boolean isSlotAvailable(Shop shop, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Récupérer tous les rendez-vous existants pour la date et le shop
        List<Appointment> existingAppointments = appointmentRepository.findByShopAndAppointmentDateAndStatusNot(
                shop, date, AppointmentStatus.CANCELED
        );

        // Vérifier si le créneau chevauche un rendez-vous existant
        return existingAppointments.stream().noneMatch(appointment ->
                startTime.isBefore(appointment.getEndTime()) && endTime.isAfter(appointment.getStartTime())
        );
    }
}
