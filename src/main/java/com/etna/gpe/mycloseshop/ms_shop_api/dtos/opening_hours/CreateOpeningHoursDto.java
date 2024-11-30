package com.etna.gpe.mycloseshop.ms_shop_api.dtos.opening_hours;

import lombok.Getter;

import java.time.DayOfWeek;

@Getter
public class CreateOpeningHoursDto {
    private DayOfWeek day;
    private String opening;
    private String closing;
}
