package com.etna.gpe.mycloseshop.ms_shop_api.services.appointment;

import com.etna.gpe.mycloseshop.common_api.ms_login.api.IUserApiFeign;
import com.etna.gpe.mycloseshop.common_api.ms_login.dto.UserDtoWithRoles;
import com.etna.gpe.mycloseshop.common_api.ms_login.dto.success.ResponseSuccess;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentResponse;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.CreateAppointmentRequest;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.AppointmentStatus;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Service;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import com.etna.gpe.mycloseshop.ms_shop_api.events.AppointmentCreatedEvent;
import com.etna.gpe.mycloseshop.ms_shop_api.mappers.IAppointmentMapper;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IAppointmentRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IServiceRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IShopRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.utils.appointments.ISortedHours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Service
public class AppointmentServiceImpl implements IAppointmentService {

    public static final String SHOP_NOT_FOUND = "Shop not found";
    // init logger
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final IAppointmentRepository appointmentRepository;
    private final IShopRepository shopRepository;
    private final IServiceRepository serviceRepository;
    private final IAppointmentMapper appointmentMapper;
    private final RabbitTemplate rabbitTemplate;
    private final IUserApiFeign userController;
    private final ISortedHours sortedHours;

    public AppointmentServiceImpl(
            IAppointmentRepository appointmentRepository,
            IShopRepository shopRepository,
            IServiceRepository serviceRepository,
            IAppointmentMapper appointmentMapper,
            RabbitTemplate rabbitTemplate,
            IUserApiFeign userController,
            ISortedHours sortedHours
    ) {
        this.appointmentRepository = appointmentRepository;
        this.shopRepository = shopRepository;
        this.serviceRepository = serviceRepository;
        this.appointmentMapper = appointmentMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.userController = userController;
        this.sortedHours = sortedHours;
    }

    @Override
    public List<LocalTime> getAvailableSlots(String shopId, LocalDate date, String serviceId) {

        Service service = serviceRepository.findById(UUID.fromString(serviceId))
                .orElseThrow(() -> new NoSuchElementException("Service not found"));

        int durationMinutes = service.getDuration();


        Shop shop = shopRepository.findById(UUID.fromString(shopId))
                .orElseThrow(() -> new NoSuchElementException(SHOP_NOT_FOUND));

        List<OpeningHours> openingHoursList = shop.getOpeningHours()
                .stream()
                .filter(oh -> oh.getDayOfWeek().name().equals(date.getDayOfWeek().name()))
                .toList();

        if (openingHoursList.isEmpty()) {
            throw new IllegalArgumentException("Shop is closed on this day");
        }


        List<Appointment> existingAppointments = appointmentRepository.findByShopAndAppointmentDateAndStatusNot(
                shop, date, AppointmentStatus.CANCELED
        );

        List<LocalTime> availableSlots = new ArrayList<>();

        for (OpeningHours openingHours : openingHoursList) {
            availableSlots.addAll(calculateAvailableSlots(openingHours.getStartTime(), openingHours.getEndTime(), durationMinutes, existingAppointments));
        }

        return sortedHours.sortHours(availableSlots);

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

        // get user info
        LOGGER.info("Starting to get user info");
        ResponseEntity<ResponseSuccess<UserDtoWithRoles>> response = userController.getUserById(request.clientId());
        UserDtoWithRoles user = Optional.ofNullable(response).map(ResponseEntity::getBody)
                .map(ResponseSuccess::getData).orElseThrow(() -> new NoSuchElementException("User not found"));
        LOGGER.info("User info fetched: {}", user);

        appointment = appointmentRepository.save(appointment);
        String Address = shop.getLocation().getAddress()
                + ", " + shop.getLocation().getPostalCode()
                + " " + shop.getLocation().getCity();

        AppointmentCreatedEvent event = AppointmentCreatedEvent.builder()
                .id(appointment.getId().toString())
                .shopName(shop.getName())
                .shopAddress(Address)
                .serviceName(service.getName())
                .serviceDescription(service.getDescription())
                .serviceDuration(service.getDuration())
                .clientEmail(user.getEmail())
                .clientUsername(user.getUsername())
                .clientFirstname(user.getFirstName())
                .clientLastname(user.getLastName())
                .date(request.date().toString())
                .startTime(request.startTime().toString())
                .endTime(endTime.toString())
                .status(AppointmentStatus.PENDING.name())
                .build();

        // send message
        rabbitTemplate.convertAndSend("appointments-exchange", "appointments.created", event);

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

    public Boolean confirmAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        return true;
    }

    @Override
    public List<AppointmentDto> getAppointmentsByClientIdAndStatus(UUID clientId, String status) {
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status);
        List<Appointment> appointmentList = appointmentRepository.findByUserIdAndStatus(clientId, appointmentStatus);

        return appointmentList.stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByShopIdAndStatus(UUID shopId, String status) {
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status);
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NoSuchElementException(SHOP_NOT_FOUND));

        List<Appointment> appointmentList = appointmentRepository.findByShopAndStatus(shop, appointmentStatus);

        return appointmentList.stream()
                .map(appointmentMapper::toDto)
                .toList();
    }
}
