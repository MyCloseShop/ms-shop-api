package com.etna.gpe.mycloseshop.ms_shop_api.enums;

import lombok.Getter;

@Getter
public enum AppointmentType {
    QUOTE("QUOTE"),
    SERVICE("SERVICE");

    private final String type;

    AppointmentType(String type) {
        this.type = type;
    }
}
