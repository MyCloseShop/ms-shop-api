package com.etna.gpe.mycloseshop.ms_shop_api.services.appointment;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.CreateAppointmentRequest;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.AppointmentStatus;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Location;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Service;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import com.etna.gpe.mycloseshop.ms_shop_api.enums.AppointmentType;
import com.etna.gpe.mycloseshop.ms_shop_api.mappers.IAppointmentMapper;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IAppointmentRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IServiceRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IShopRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.utils.appointments.SortedHoursImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    private static UUID shopId;
    @Mock
    private IAppointmentRepository appointmentRepository;
    @Mock
    private IServiceRepository serviceRepository;
    @Mock
    private IShopRepository shopRepository;
    @Mock
    private IAppointmentMapper appointmentMapper;
    @Mock
    private RabbitTemplate rabbitTemplate;

    private AppointmentServiceImpl appointmentService;

    @BeforeAll
    static void setUp() {
        shopId = UUID.randomUUID();
    }

    @BeforeEach
    void setUpEach() {
        // Utilisation de la vraie implémentation de SortedHours
        SortedHoursImpl sortedHours = new SortedHoursImpl();
        appointmentService = new AppointmentServiceImpl(
                appointmentRepository,
                shopRepository,
                serviceRepository,
                appointmentMapper,
                rabbitTemplate,
                sortedHours
        );
    }

    @Test
    @DisplayName("Should return available slots")
    void getAvailableSlotsTest_case_1() {

        LocalDate givenDate = LocalDate.parse("2024-11-11"); // Monday

        OpeningHours shopOpeningHoursMonday = new OpeningHours();
        shopOpeningHoursMonday.setDayOfWeek(DayOfWeek.valueOf("MONDAY"));
        shopOpeningHoursMonday.setStartTime(java.time.LocalTime.parse("08:00:00"));
        shopOpeningHoursMonday.setEndTime(java.time.LocalTime.parse("18:00:00"));

        Shop shop = new Shop();
        shop.setOpeningHours(List.of(shopOpeningHoursMonday));

        // Given
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(appointmentRepository.findByShopAndAppointmentDateAndStatusNot(
                any(Shop.class), any(LocalDate.class), any(AppointmentStatus.class))
        ).thenReturn(new ArrayList<>());

        // When
        List<LocalTime> availableSlots = appointmentService.getAvailableSlots(shopId.toString(), givenDate, 30);

        // Then
        assertThat(availableSlots).isNotNull().isNotEmpty().hasSize(20);
    }

    @Test
    @DisplayName("Should return available slots even if there are existing appointments")
    void calculateAvailableSlotsTest_case_2() {
        // Given
        Shop shop = new Shop();
        OpeningHours shopOpeningHoursMonday = new OpeningHours();

        LocalTime shopStartTime = LocalTime.parse("08:00:00");
        LocalTime shopEndTime = LocalTime.parse("18:00:00");
        shopOpeningHoursMonday.setStartTime(shopStartTime);
        shopOpeningHoursMonday.setEndTime(shopEndTime);
        shopOpeningHoursMonday.setDayOfWeek(DayOfWeek.valueOf("MONDAY"));
        shopOpeningHoursMonday.setShop(shop);

        shop.setOpeningHours(List.of(shopOpeningHoursMonday));

        LocalDate givenDate = LocalDate.parse("2024-11-11");

        List<Appointment> existingAppointments = new ArrayList<>();

        Appointment appointment1 = new Appointment();
        appointment1.setStartTime(LocalTime.parse("08:00:00"));
        appointment1.setEndTime(LocalTime.parse("08:30:00"));

        existingAppointments.add(appointment1);

        // Given
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(appointmentRepository.findByShopAndAppointmentDateAndStatusNot(
                shop, givenDate, AppointmentStatus.CANCELED)
        ).thenReturn(existingAppointments);

        // When
        List<LocalTime> availableSlots = appointmentService.getAvailableSlots(shopId.toString(), givenDate, 30);

        // Then
        assertThat(availableSlots).isNotNull().isNotEmpty().hasSize(19)
                .doesNotContain(LocalTime.parse("08:00:00"));
    }

    @Test
    @DisplayName("Should return the good list of available slots")
    void calculateAvailableSlotsTest_case_3() {
        LocalDate givenDate = LocalDate.parse("2024-11-11"); // Monday

        OpeningHours shopOpeningHoursMonday1 = new OpeningHours();
        shopOpeningHoursMonday1.setDayOfWeek(DayOfWeek.valueOf("MONDAY"));
        shopOpeningHoursMonday1.setStartTime(java.time.LocalTime.parse("08:00:00"));
        shopOpeningHoursMonday1.setEndTime(java.time.LocalTime.parse("12:00:00"));

        OpeningHours shopOpeningHoursMonday2 = new OpeningHours();
        shopOpeningHoursMonday2.setDayOfWeek(DayOfWeek.valueOf("MONDAY"));
        shopOpeningHoursMonday2.setStartTime(java.time.LocalTime.parse("13:00:00"));
        shopOpeningHoursMonday2.setEndTime(java.time.LocalTime.parse("15:00:00"));

        OpeningHours shopOpeningHoursMonday3 = new OpeningHours();
        shopOpeningHoursMonday3.setDayOfWeek(DayOfWeek.valueOf("MONDAY"));
        shopOpeningHoursMonday3.setStartTime(java.time.LocalTime.parse("18:00:00"));
        shopOpeningHoursMonday3.setEndTime(java.time.LocalTime.parse("19:00:00"));

        Shop shop = new Shop();
        shop.setOpeningHours(
                List.of(shopOpeningHoursMonday1, shopOpeningHoursMonday2, shopOpeningHoursMonday3)
        );

        List<LocalTime> expectedSlots = List.of(
                LocalTime.parse("08:00:00"),
                LocalTime.parse("08:30:00"),
                LocalTime.parse("09:00:00"),
                LocalTime.parse("09:30:00"),
                LocalTime.parse("10:00:00"),
                LocalTime.parse("10:30:00"),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("11:30:00"),
                LocalTime.parse("13:00:00"),
                LocalTime.parse("13:30:00"),
                LocalTime.parse("14:00:00"),
                LocalTime.parse("14:30:00"),
                LocalTime.parse("18:00:00"),
                LocalTime.parse("18:30:00")
        );

        // Given
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(appointmentRepository.findByShopAndAppointmentDateAndStatusNot(
                any(Shop.class), any(LocalDate.class), any(AppointmentStatus.class))
        ).thenReturn(new ArrayList<>());

        // When
        List<LocalTime> availableSlots = appointmentService.getAvailableSlots(shopId.toString(), givenDate, 30);

        // Then
        assertThat(availableSlots).isNotNull().isNotEmpty().hasSize(14).containsExactlyElementsOf(expectedSlots);
    }

    @Test()
    @DisplayName("Should throw NoSuchElementException when the shop does not exist when he try to create an appointment")
    void createAppointmentTest_case_1() {
        // Given
        Shop shop = new Shop();
        // Il faut ajouter une Location au shop car elle est utilisée dans createAppointment
        Location location = new Location();
        location.setAddress("123 Main St");
        location.setPostalCode("75001");
        location.setCity("Paris");
        shop.setLocation(location);
        shop.setName("Test Shop");

        OpeningHours shopOpeningHoursMonday = new OpeningHours();
        LocalTime shopStartTime = LocalTime.parse("08:00:00");
        LocalTime shopEndTime = LocalTime.parse("18:00:00");
        shopOpeningHoursMonday.setStartTime(shopStartTime);
        shopOpeningHoursMonday.setEndTime(shopEndTime);
        shopOpeningHoursMonday.setDayOfWeek(DayOfWeek.valueOf("MONDAY"));
        shopOpeningHoursMonday.setShop(shop);

        shop.setOpeningHours(List.of(shopOpeningHoursMonday));
        shop.setId(shopId);

        Service serviceChosen = new Service();
        int durationMinutes = 30;
        serviceChosen.setDuration(durationMinutes);
        serviceChosen.setName("Test Service");

        LocalDate givenDate = LocalDate.parse("2024-11-11");

        UUID clientUUID = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                .shopId(shopId)
                .serviceId(serviceId)
                .clientId(clientUUID)
                .appointmentType(AppointmentType.SERVICE) // Ajout du type obligatoire
                .startTime(LocalTime.parse("10:00:00"))
                .date(givenDate)
                .build();

        // Mocks
        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> appointmentService.createAppointment(request));
    }
}
