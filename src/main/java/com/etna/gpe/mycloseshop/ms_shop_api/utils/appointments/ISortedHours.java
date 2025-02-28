package com.etna.gpe.mycloseshop.ms_shop_api.utils.appointments;

import java.time.LocalTime;
import java.util.List;

public interface ISortedHours {
    List<LocalTime> sortHours(List<LocalTime> hours);
}
