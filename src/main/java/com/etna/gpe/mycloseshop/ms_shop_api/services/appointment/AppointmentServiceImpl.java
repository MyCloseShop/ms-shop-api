package com.etna.gpe.mycloseshop.ms_shop_api.services.appointment;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.CreateAppointmentRequest;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.AppointmentStatus;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Service;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import com.etna.gpe.mycloseshop.ms_shop_api.enums.AppointmentType;
import com.etna.gpe.mycloseshop.ms_shop_api.events.AppointmentCreatedEvent;
import com.etna.gpe.mycloseshop.ms_shop_api.mappers.IAppointmentMapper;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IAppointmentRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IServiceRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IShopRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.utils.appointments.ISortedHours;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@org.springframework.stereotype.Service
public class AppointmentServiceImpl implements IAppointmentService {

    public static final String SHOP_NOT_FOUND = "Shop not found";
    // init logger
    private final IAppointmentRepository appointmentRepository;
    private final IShopRepository shopRepository;
    private final IServiceRepository serviceRepository;
    private final IAppointmentMapper appointmentMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ISortedHours sortedHours;

    public AppointmentServiceImpl(
            IAppointmentRepository appointmentRepository,
            IShopRepository shopRepository,
            IServiceRepository serviceRepository,
            IAppointmentMapper appointmentMapper,
            RabbitTemplate rabbitTemplate,
            ISortedHours sortedHours
    ) {
        this.appointmentRepository = appointmentRepository;
        this.shopRepository = shopRepository;
        this.serviceRepository = serviceRepository;
        this.appointmentMapper = appointmentMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.sortedHours = sortedHours;
    }

    @Override
    public List<LocalTime> getAvailableSlots(String shopId, LocalDate date, Integer duration) {
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
            availableSlots.addAll(calculateAvailableSlots(openingHours.getStartTime(), openingHours.getEndTime(), duration, existingAppointments));
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

    @Override
    public AppointmentDto createAppointment(CreateAppointmentRequest request) {
        // 1. Récupérer le magasin
        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new NoSuchElementException(SHOP_NOT_FOUND));

        Service service = null;
        UUID quoteId = null;
        LocalTime endTime;

        // 2. Traiter selon le type de rendez-vous
        switch (request.appointmentType()) {
            case SERVICE -> {
                // Validation pour le type SERVICE
                if (request.serviceId() == null) {
                    throw new IllegalArgumentException("Service ID is required for service appointment");
                }

                // Récupérer le service
                service = serviceRepository.findById(request.serviceId())
                        .orElseThrow(() -> new NoSuchElementException("Service not found"));

                // Calculer l'heure de fin basée sur la durée du service
                endTime = request.startTime().plusMinutes(service.getDuration());
            }
            case QUOTE -> {
                // Validation pour le type QUOTE
                if (request.serviceId() == null) {
                    throw new IllegalArgumentException("Quote ID is required for quote appointment");
                }

                // Stocker l'ID du devis
                quoteId = request.serviceId();

                // Pour un devis, on définit une durée standard (par exemple 30 minutes)
                // Cette durée pourrait venir d'une configuration ou d'une constante

                if (request.duration() == null || request.duration() <= 0) {
                    throw new IllegalArgumentException("Duration must be a positive integer for quote appointment");
                }

                endTime = request.startTime().plusMinutes(request.duration());
            }
            default -> throw new IllegalArgumentException("Invalid appointment type");
        }

        // 3. Vérifier la disponibilité du créneau
        boolean isSlotAvailable = isSlotAvailable(shop, request.date(), request.startTime(), endTime);
        if (!isSlotAvailable) {
            throw new IllegalArgumentException("The requested slot is already booked");
        }

        // 4. Créer et sauvegarder le rendez-vous
        Appointment appointment = new Appointment();
        appointment.setShop(shop);
        appointment.setType(request.appointmentType());
        appointment.setService(AppointmentType.SERVICE.equals(request.appointmentType()) ? service : null);
        appointment.setQuoteId(AppointmentType.QUOTE.equals(request.appointmentType()) ? quoteId : null);
        appointment.setUserId(request.clientId());
        appointment.setAppointmentDate(request.date());
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.PENDING);

        appointment = appointmentRepository.save(appointment);

        String Address = shop.getLocation().getAddress()
                + ", " + shop.getLocation().getPostalCode()
                + " " + shop.getLocation().getCity();

        // 5. Créer et envoyer l'événement
        AppointmentCreatedEvent.AppointmentCreatedEventBuilder eventBuilder = AppointmentCreatedEvent.builder()
                .id(appointment.getId().toString())
                .shopName(shop.getName())
                .shopAddress(Address)
                .clientId(appointment.getUserId().toString())
                .date(request.date().toString())
                .startTime(request.startTime().toString())
                .endTime(endTime.toString())
                .status(AppointmentStatus.PENDING.name());

        // Ajouter les informations spécifiques selon le type
        if (service != null) {
            eventBuilder
                    .serviceName(service.getName())
                    .serviceDuration(service.getDuration())
                    .serviceDescription(service.getDescription());
        }
        /*else if (quoteId != null) {
            eventBuilder.quoteId(quoteId.toString());
        }*/

        //AppointmentCreatedEvent event = eventBuilder.build();

        // Envoyer le message
        // NOTE: Now once the appointment is created, in base, the confirmation is done when the appiontment is paid
        //rabbitTemplate.convertAndSend("appointments-exchange", "appointments.created", event);

        // 6. Retourner la réponse
        return appointmentMapper.toDto(appointment);
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

    @Transactional
    public Boolean paidAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Appointment not found"));
        // Check if the appointment is already confirmed
        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Appointment is already confirmed");
        }
        // Check if the appointment is canceled
        if (appointment.getStatus() == AppointmentStatus.CANCELED) {
            throw new IllegalStateException("Appointment is canceled and cannot be paid");
        }
        // Check if the appointment is already paid
        if (appointment.getStatus() == AppointmentStatus.PAID) {
            throw new IllegalStateException("Appointment is already paid");
        }

        // Update the appointment status to PAID
        appointment.setStatus(AppointmentStatus.PAID);
        appointmentRepository.save(appointment);

        // Publish the event to RabbitMQ
        AppointmentCreatedEvent event = AppointmentCreatedEvent.builder()
                .id(appointment.getId().toString())
                .shopName(appointment.getShop().getName())
                .shopAddress(appointment.getShop().getLocation().getAddress()
                        + ", " + appointment.getShop().getLocation().getPostalCode()
                        + " " + appointment.getShop().getLocation().getCity())
                .clientId(appointment.getUserId().toString())
                .date(appointment.getAppointmentDate().toString())
                .startTime(appointment.getStartTime().toString())
                .endTime(appointment.getEndTime().toString())
                .status(AppointmentStatus.PAID.name())
                .build();

        // if appointment type is SERVICE, add service details
        if (appointment.getType() == AppointmentType.SERVICE && appointment.getService() != null) {
            event = event.toBuilder()
                    .serviceId(appointment.getService().getId().toString())
                    .serviceName(appointment.getService().getName())
                    .serviceDuration(appointment.getService().getDuration())
                    .serviceDescription(appointment.getService().getDescription())
                    .build();
        }

        // if appointment type is QUOTE, add quote details
        if (appointment.getType() == AppointmentType.QUOTE && appointment.getQuoteId() != null) {
            event = event.toBuilder()
                    .quoteId(appointment.getQuoteId().toString())
                    .build();
        }

        rabbitTemplate.convertAndSend("appointments-exchange", "appointments.paid", event);
        return true;
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
