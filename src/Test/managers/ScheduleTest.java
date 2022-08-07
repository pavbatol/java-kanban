package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleTest {
    Schedule schedule;

    @BeforeEach
    void setUp() {
        schedule = new Schedule(15);
    }

    @Test
    void temp() {
        long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        String startStr = String.format("start: %7d, %s\n" , start, LocalDateTime.now());

//        System.out.println(schedule.occupieds.size());
//        schedule.occupieds.forEach((k, v) -> System.out.printf("%s = %s%n", k, v));

//        schedule.occupiedTimes.forEach((k, v) -> System.out.printf("%s = %s%n", k, v));

//        long count = schedule.occupiedTimes.entrySet().stream()
//                .limit(20)
//                .peek(System.out::println)
//                .count();
//        System.out.println(count);

        List<String> times = new ArrayList<>(schedule.occupiedTimes.keySet());
        times.sort((o1, o2) -> LocalDateTime.parse(o1).isAfter(LocalDateTime.parse(o2)) ? 1
                : LocalDateTime.parse(o1).isBefore(LocalDateTime.parse(o2)) ? -1 : 0);

//        times.forEach(System.out::println);

        long count = times.stream()
                        //.takeWhile(s -> s.contains("2022"))
                        .limit(20)
                        .peek(System.out::println)
                        .count();
        System.out.println(count);

        long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        System.out.printf(startStr);
        System.out.printf("end: %15d, %s\n" , end, LocalDateTime.now());
        System.out.println("Заняло: " + ((end - start) / 1000.) + " sec");
    }

    @Test
    void occupy() {
        long start_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        String startStr = String.format("start: %7d, %s\n" , start_0, LocalDateTime.now());

        LocalDateTime start = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0,
                0);
        LocalDateTime end = start.plusMinutes(schedule.timeStep * 2L);

        // Проверяем
        assertTrue(schedule.occupy(start, end), "Время не свободно");
        assertTrue(schedule.occupy(end, end.plusMinutes(schedule.timeStep * 2L)), "Время не свободно");
        assertFalse(schedule.occupy(end, end.plusMinutes(schedule.timeStep * 3L)), "Время свободно");

        // Печать
        long end_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        System.out.printf(startStr);
        System.out.printf("end: %15d, %s\n" , end_0, LocalDateTime.now());
        System.out.println("Заняло: " + ((end_0 - start_0) / 1000.) + " sec");

        long count = schedule.occupiedTimes.entrySet().stream()
                .filter(Map.Entry::getValue)
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                                : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .peek(System.out::println)
                .count();
        System.out.println(count);

    }
}