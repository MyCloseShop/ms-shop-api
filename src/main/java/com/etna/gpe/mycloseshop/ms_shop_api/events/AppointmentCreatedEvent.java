package com.etna.gpe.mycloseshop.ms_shop_api.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentCreatedEvent {
    private String id;
    private String shopName;
    private String shopAddress;
    private String serviceName;
    private Integer serviceDuration;
    private String clientEmail;
    private String clientUsername;
    private String clientFirstname;
    private String clientLastname;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
}
