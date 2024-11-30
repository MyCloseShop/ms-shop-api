package com.etna.gpe.mycloseshop.ms_shop_api.repository;

import com.etna.gpe.mycloseshop.ms_shop_api.entity.Appointment;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.AppointmentStatus;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IAppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByShopAndAppointmentDate(Shop shop, LocalDate appointmentDate);

    List<Appointment> findByUserId(UUID userId);

    List<Appointment> findByShopAndAppointmentDateAndStatusNot(Shop shop, LocalDate appointmentDate, AppointmentStatus status);

    List<Appointment> findByShop(Shop shop);

    List<Appointment> findByUserIdAndStatus(UUID userId, AppointmentStatus status);
}
