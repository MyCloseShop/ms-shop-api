package com.etna.gpe.mycloseshop.ms_shop_api.dtos;

import com.etna.gpe.mycloseshop.ms_shop_api.entity.DayOfWeek;

public class CreateOpeningHoursDto {
    private DayOfWeek day;
    private String opening;
    private String closing;

    public DayOfWeek getDay() {
        return day;
    }

    public String getOpening() {
        return opening;
    }

    public String getClosing() {
        return closing;
    }
}
