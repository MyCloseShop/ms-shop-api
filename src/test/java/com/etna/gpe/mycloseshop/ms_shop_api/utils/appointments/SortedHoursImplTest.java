package com.etna.gpe.mycloseshop.ms_shop_api.utils.appointments;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortedHoursImplTest {
    private static Stream<Map<List<LocalTime>, List<LocalTime>>> testSortHoursData() {
        return Stream.of(
                Map.of(List.of(LocalTime.of(10, 0), LocalTime.of(9, 0), LocalTime.of(11, 0)),
                        List.of(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0))),
                Map.of(List.of(LocalTime.of(10, 0), LocalTime.of(9, 0), LocalTime.of(11, 0), LocalTime.of(6, 0)),
                        List.of(LocalTime.of(6, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0))),
                Map.of(List.of(LocalTime.of(10, 0), LocalTime.of(9, 0), LocalTime.of(11, 0), LocalTime.of(6, 0), LocalTime.of(7, 0)),
                        List.of(LocalTime.of(6, 0), LocalTime.of(7, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0)))
        );
    }

    @ParameterizedTest(name = "Test sortHours with {0}")
    @DisplayName("Test sortHours")
    @MethodSource("testSortHoursData")
    void testSortHours(
            Map<List<LocalTime>, List<LocalTime>> data
    ) {
        ISortedHours sortedHours = new SortedHoursImpl();
        List<LocalTime> hours = data.keySet().iterator().next();
        List<LocalTime> expected = data.get(hours);
        List<LocalTime> actual = sortedHours.sortHours(hours);

        assertEquals(expected, actual);

    }
}