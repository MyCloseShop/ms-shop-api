package com.etna.gpe.mycloseshop.ms_shop_api.listeners.appointments;

import com.etna.gpe.mycloseshop.ms_shop_api.events.AppointmentCreatedEvent;

public interface IAppointmentMailSentListener {
    void onAppointmentMailSent(AppointmentCreatedEvent event);
}
