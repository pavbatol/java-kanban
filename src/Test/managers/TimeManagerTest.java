package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TimeManagerTest {
    TimeManager timeManager;

    @BeforeEach
    void setUp() {
        timeManager = new TimeManager(15);
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
        LocalDateTime end = start.plusMinutes(timeManager.timeStep * 2L);

        // Проверяем
        assertTrue(timeManager.occupy(start, end, true), "Время не свободно");
        assertTrue(timeManager.occupy(end, end.plusMinutes(timeManager.timeStep * 2L), true), "Время не свободно");
        assertFalse(timeManager.occupy(end, end.plusMinutes(timeManager.timeStep * 3L), true), "Время свободно");
        assertFalse(timeManager.occupy(end, end.plusMinutes(timeManager.timeStep), true), "Время свободно");

        // Печать
        long count = timeManager.times.entrySet().stream()
                .filter(Map.Entry::getValue)
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                                : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .peek(System.out::println)
                .count();

        System.out.println("count = " +count + "\n");

        long end_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        System.out.printf(startStr);
        System.out.printf("end: %15d, %s\n" , end_0, LocalDateTime.now());
        System.out.println("Заняло: " + ((end_0 - start_0) / 1000.) + " sec");
    }

    @Test
    void free() {
        long start_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        String startStr = String.format("start: %7d, %s\n" , start_0, LocalDateTime.now());

        LocalDateTime start = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0,
                0);
        LocalDateTime end = start.plusMinutes(timeManager.timeStep * 2L);

        // Проверяем что есть занятые
        assertTrue(timeManager.occupy(start, end, true), "Время не свободно");
        assertTrue(timeManager.occupy(end, end.plusMinutes(timeManager.timeStep * 3L), true),
                "Время не свободно");

        long count = timeManager.times.entrySet().stream()
                .filter(Map.Entry::getValue)
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                        : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .peek(System.out::println)
                .count();
        System.out.println("count = " +count + "\n");

        assertEquals(5, count); // 5 Занятых ячеек времени (2+3)

        // Проверяем что освободили
        assertTrue(timeManager.free(start, end), "Не освободили время");

        count = timeManager.times.entrySet().stream()
                .filter(Map.Entry::getValue)
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                        : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .peek(System.out::println)
                .count();
        System.out.println("count = " +count + "\n");

        assertEquals(3, count); // 3 Занятых ячеек времени (2 освободили)

        assertTrue(timeManager.free(start, end.plusMinutes(timeManager.timeStep * 3L)), "Не освободили время");

        count = timeManager.times.entrySet().stream()
                .filter(Map.Entry::getValue)
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                        : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .peek(System.out::println)
                .count();
        System.out.println("count = " +count + "\n");

        assertEquals(0, count); // 0 Занятых ячеек времени (3 освободили)

        // Печать
        long end_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        System.out.printf(startStr);
        System.out.printf("end: %15d, %s\n" , end_0, LocalDateTime.now());
        System.out.println("Заняло: " + ((end_0 - start_0) / 1000.) + " sec");
    }

    @Test
    void reset() {
        LocalDateTime start = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0,
                0);
        LocalDateTime end = start.plusMinutes(timeManager.timeStep * 2L);

        // Проверяем что есть занятые
        assertTrue(timeManager.occupy(start, end, true), "Время не свободно");
        assertTrue(timeManager.occupy(end, end.plusMinutes(timeManager.timeStep * 3L), true),
                "Время не свободно");

        timeManager.reset();
        long count = timeManager.times.values().stream()
                .filter(v -> v)
                .count();
        assertEquals(0, count, "Количество элементов не равно");

    }
}