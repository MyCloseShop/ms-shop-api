package com.etna.gpe.mycloseshop.ms_shop_api.mappers;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.AppointmentStatus;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Location;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Service;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import com.etna.gpe.mycloseshop.ms_shop_api.enums.AppointmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IAppointmentMapperTest {

    private IAppointmentMapper mapper;
    private Shop testShop;
    private Service testService;
    private Location testLocation;
    private OpeningHours testOpeningHours;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(IAppointmentMapper.class);

        // Setup test data
        testLocation = new Location();
        testLocation.setId(UUID.randomUUID());
        testLocation.setAddress("123 Test Street");
        testLocation.setCity("Test City");
        testLocation.setPostalCode("12345");

        testOpeningHours = new OpeningHours();
        testOpeningHours.setId(UUID.randomUUID());
        testOpeningHours.setDayOfWeek(DayOfWeek.MONDAY);
        testOpeningHours.setStartTime(LocalTime.of(9, 0));
        testOpeningHours.setEndTime(LocalTime.of(17, 0));

        testShop = new Shop();
        testShop.setId(UUID.randomUUID());
        testShop.setName("Test Shop");
        testShop.setUserId(UUID.randomUUID());
        testShop.setLocation(testLocation);
        testShop.setOpeningHours(List.of(testOpeningHours));

        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setName("Test Service");
        testService.setDescription("Test Description");
        testService.setDuration(30);
        testService.setPrice(50.0);
    }

    @Test
    @DisplayName("Should map serviceId from service.id when service exists and has id")
    void testToDto_WithServiceId_ShouldReturnServiceId() {
        // Given
        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.SERVICE);
        appointment.setService(testService);
        appointment.setQuoteId(null);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.appointmentType()).isEqualTo(AppointmentType.SERVICE);
        assertThat(result.serviceId()).isEqualTo(testService.getId());
        assertThat(result.id()).isEqualTo(appointment.getId());
        assertThat(result.clientId()).isEqualTo(appointment.getUserId());
        assertThat(result.date()).isEqualTo(appointment.getAppointmentDate());
        assertThat(result.startTime()).isEqualTo(appointment.getStartTime());
        assertThat(result.endTime()).isEqualTo(appointment.getEndTime());
        assertThat(result.status()).isEqualTo(appointment.getStatus());

        // Vérifier les mappings du shop
        assertThat(result.shop()).isNotNull();
        assertThat(result.shop().getShopId()).isEqualTo(testShop.getId());
        assertThat(result.shop().getOwnerId()).isEqualTo(testShop.getUserId());
        assertThat(result.shop().getLocationId()).isEqualTo(testLocation.getId());
        assertThat(result.shop().getOpeningHoursIds()).containsExactly(testOpeningHours.getId());
    }

    @Test
    @DisplayName("Should map serviceId from quoteId when appointment type is QUOTE")
    void testToDto_WithQuoteId_ShouldReturnQuoteId() {
        // Given
        UUID quoteId = UUID.randomUUID();
        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.QUOTE);
        appointment.setService(null);
        appointment.setQuoteId(quoteId);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.appointmentType()).isEqualTo(AppointmentType.QUOTE);
        assertThat(result.serviceId()).isEqualTo(quoteId);
        assertThat(result.id()).isEqualTo(appointment.getId());
        assertThat(result.clientId()).isEqualTo(appointment.getUserId());
        assertThat(result.date()).isEqualTo(appointment.getAppointmentDate());
        assertThat(result.startTime()).isEqualTo(appointment.getStartTime());
        assertThat(result.endTime()).isEqualTo(appointment.getEndTime());
        assertThat(result.status()).isEqualTo(appointment.getStatus());
    }

    @Test
    @DisplayName("Should map serviceId from quoteId when service exists but has null id")
    void testToDto_WithServiceWithNullId_ShouldReturnQuoteId() {
        // Given
        UUID quoteId = UUID.randomUUID();
        Service serviceWithNullId = new Service();
        serviceWithNullId.setId(null);
        serviceWithNullId.setName("Service Without ID");

        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.QUOTE);
        appointment.setService(serviceWithNullId);
        appointment.setQuoteId(quoteId);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.appointmentType()).isEqualTo(AppointmentType.QUOTE);
        assertThat(result.serviceId()).isEqualTo(quoteId);
    }

    @Test
    @DisplayName("Should return null serviceId when both service and quoteId are null")
    void testToDto_WithBothNull_ShouldReturnNullServiceId() {
        // Given
        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.SERVICE); // Même si c'est SERVICE, pas de service défini
        appointment.setService(null);
        appointment.setQuoteId(null);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.appointmentType()).isEqualTo(AppointmentType.SERVICE);
        assertThat(result.serviceId()).isNull();
    }

    @Test
    @DisplayName("Should prioritize service.id over quoteId when both exist and type is SERVICE")
    void testToDto_WithBothServiceAndQuote_ShouldPrioritizeServiceId() {
        // Given
        UUID quoteId = UUID.randomUUID();
        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.SERVICE);
        appointment.setService(testService);
        appointment.setQuoteId(quoteId);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.appointmentType()).isEqualTo(AppointmentType.SERVICE);
        assertThat(result.serviceId()).isEqualTo(testService.getId());
        assertThat(result.serviceId()).isNotEqualTo(quoteId);
    }

    @Test
    @DisplayName("Should map QUOTE type correctly and use quoteId even when service exists")
    void testToDto_WithQuoteTypeAndServicePresent_ShouldUseQuoteId() {
        // Given
        UUID quoteId = UUID.randomUUID();
        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.QUOTE);
        appointment.setService(testService); // Service présent mais ignoré car type = QUOTE
        appointment.setQuoteId(quoteId);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.appointmentType()).isEqualTo(AppointmentType.QUOTE);
        // Maintenant avec la nouvelle logique, on utilise quoteId pour les types QUOTE
        assertThat(result.serviceId()).isEqualTo(quoteId);
        assertThat(result.serviceId()).isNotEqualTo(testService.getId());
    }

    @Test
    @DisplayName("Should return null for SERVICE type when service is null")
    void testToDto_WithServiceTypeButNoService_ShouldReturnNull() {
        // Given
        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.SERVICE);
        appointment.setService(null);
        appointment.setQuoteId(UUID.randomUUID()); // QuoteId présent mais ignoré car type = SERVICE

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.appointmentType()).isEqualTo(AppointmentType.SERVICE);
        assertThat(result.serviceId()).isNull(); // Pas de service.id disponible
    }

    @Test
    @DisplayName("Should return null for QUOTE type when quoteId is null")
    void testToDto_WithQuoteTypeButNoQuoteId_ShouldReturnNull() {
        // Given
        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.QUOTE);
        appointment.setService(testService); // Service présent mais ignoré car type = QUOTE
        appointment.setQuoteId(null);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.appointmentType()).isEqualTo(AppointmentType.QUOTE);
        assertThat(result.serviceId()).isNull(); // Pas de quoteId disponible
    }

    @Test
    @DisplayName("Should handle empty opening hours list")
    void testToDto_WithEmptyOpeningHours_ShouldReturnEmptyList() {
        // Given
        testShop.setOpeningHours(List.of());
        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.SERVICE); // Ajouter le type obligatoire
        appointment.setService(testService);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.shop().getOpeningHoursIds()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null opening hours list")
    void testToDto_WithNullOpeningHours_ShouldReturnEmptyList() {
        // Given
        testShop.setOpeningHours(null);
        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.SERVICE); // Ajouter le type obligatoire
        appointment.setService(testService);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.shop().getOpeningHoursIds()).isEmpty();
    }

    @Test
    @DisplayName("Should map multiple opening hours correctly")
    void testToDto_WithMultipleOpeningHours_ShouldMapAllIds() {
        // Given
        OpeningHours secondOpeningHours = new OpeningHours();
        secondOpeningHours.setId(UUID.randomUUID());
        secondOpeningHours.setDayOfWeek(DayOfWeek.TUESDAY);
        secondOpeningHours.setStartTime(LocalTime.of(10, 0));
        secondOpeningHours.setEndTime(LocalTime.of(18, 0));

        testShop.setOpeningHours(List.of(testOpeningHours, secondOpeningHours));

        Appointment appointment = createBaseAppointment();
        appointment.setType(AppointmentType.SERVICE); // Ajouter le type obligatoire
        appointment.setService(testService);

        // When
        AppointmentDto result = mapper.toDto(appointment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.shop().getOpeningHoursIds())
                .hasSize(2)
                .containsExactlyInAnyOrder(testOpeningHours.getId(), secondOpeningHours.getId());
    }

    private Appointment createBaseAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setShop(testShop);
        appointment.setUserId(UUID.randomUUID());
        appointment.setAppointmentDate(LocalDate.of(2024, 11, 11));
        appointment.setStartTime(LocalTime.of(10, 0));
        appointment.setEndTime(LocalTime.of(10, 30));
        appointment.setStatus(AppointmentStatus.PENDING);
        return appointment;
    }
}
