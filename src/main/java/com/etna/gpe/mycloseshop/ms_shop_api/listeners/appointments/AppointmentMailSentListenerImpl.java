package com.etna.gpe.mycloseshop.ms_shop_api.listeners.appointments;

import com.etna.gpe.mycloseshop.ms_shop_api.events.AppointmentCreatedEvent;
import com.etna.gpe.mycloseshop.ms_shop_api.services.appointment.IAppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AppointmentMailSentListenerImpl implements IAppointmentMailSentListener {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentMailSentListenerImpl.class);
    private final IAppointmentService iAppointmentService;

    public AppointmentMailSentListenerImpl(IAppointmentService iAppointmentService) {
        this.iAppointmentService = iAppointmentService;
    }

    @Override
    @RabbitListener(queues = "mail-sent-queue")
    public void onAppointmentMailSent(AppointmentCreatedEvent event) {
        logger.info("Message received: {}", event);

        Boolean isAppointmentConfirmed = false;

        // change de state of the appointment pending to confirmed
        isAppointmentConfirmed = iAppointmentService.confirmAppointment(UUID.fromString(event.getId()));

        if (Boolean.TRUE.equals(isAppointmentConfirmed)) {
            logger.info("Appointment confirmed");
        } else {
            logger.error("Failed to confirm appointment");
        }
    }
}
