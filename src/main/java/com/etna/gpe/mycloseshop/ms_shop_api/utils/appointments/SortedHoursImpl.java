package com.etna.gpe.mycloseshop.ms_shop_api.utils.appointments;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SortedHoursImpl implements ISortedHours {
    @Override
    public List<LocalTime> sortHours(List<LocalTime> hours) {

        List<LocalTime> sortedHours = new ArrayList<>(hours);

        sortedHours.sort(LocalTime::compareTo);

        return sortedHours;
    }
}
