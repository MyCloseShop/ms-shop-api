package com.etna.gpe.mycloseshop.ms_shop_api.services.appointment;

import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.AppointmentStatus;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Service;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IAppointmentRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IServiceRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IShopRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @BeforeAll
    static void setUp() {
        shopId = UUID.randomUUID();
    }


    @Test
    @DisplayName("Should return available slots")
    void getAvailableSlotsTest_case_1() {

        LocalDate givenDate = LocalDate.parse("2024-11-11"); // Monday

        OpeningHours shopOpeningHoursMonday = new OpeningHours();
        shopOpeningHoursMonday.setDayOfWeek(DayOfWeek.valueOf("MONDAY"));
        shopOpeningHoursMonday.setStartTime(java.time.LocalTime.parse("08:00:00"));
        shopOpeningHoursMonday.setEndTime(java.time.LocalTime.parse("18:00:00"));

        Service shopService = new Service();
        shopService.setDuration(30);

        Shop shop = new Shop();
        shop.setOpeningHours(List.of(shopOpeningHoursMonday));


        // Given
        when(serviceRepository.findById(any(UUID.class))).thenReturn(Optional.of(shopService));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(new Shop()));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(appointmentRepository.findByShopAndAppointmentDateAndStatusNot(
                any(Shop.class), any(LocalDate.class), any(AppointmentStatus.class))
        ).thenReturn(new ArrayList<>());
        // When
        List<LocalTime> availableSlots = appointmentService.getAvailableSlots(shopId.toString(), givenDate, UUID.randomUUID().toString());

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

        Service serviceChosen = new Service();
        int durationMinutes = 30;

        serviceChosen.setDuration(durationMinutes);

        LocalDate givenDate = LocalDate.parse("2024-11-11");


        List<Appointment> existingAppointments = new ArrayList<>();

        Appointment appointment1 = new Appointment();
        appointment1.setStartTime(LocalTime.parse("08:00:00"));
        appointment1.setEndTime(LocalTime.parse("08:30:00"));

        existingAppointments.add(appointment1);


        when(serviceRepository.findById(any(UUID.class))).thenReturn(Optional.of(serviceChosen));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(appointmentRepository.findByShopAndAppointmentDateAndStatusNot(
                shop, givenDate, AppointmentStatus.CANCELED)
        ).thenReturn(existingAppointments);

        // When
        List<LocalTime> availableSlots = appointmentService.getAvailableSlots(shopId.toString(), givenDate, UUID.randomUUID().toString());

        // Then
        assertThat(availableSlots).isNotNull().isNotEmpty().hasSize(19)
                .doesNotContain(LocalTime.parse("08:00:00"));
    }
}