package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.NEW;

class TimeManagerTest {
    TimeManager timeManager;

    @BeforeEach
    void setUp() {
        timeManager = new TimeManager(15);
    }

    @Test
     void occupyFor() {
        long start_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        String startStr = String.format("start: %7d, %s\n" , start_0, LocalDateTime.now());

        final int timeStep = timeManager.getTimeStep();
        LocalDateTime start = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0, 0);

        Task task1 = new Task("Name1", "Description1", NEW);
        Task task2 = new Task("Name2", "Description2", NEW);
        Task task3 = new Task("Name3", "Description3", NEW);
        Task task4 = new Task("Name4", "Description4", NEW);
        task1.setId(0);
        task2.setId(1);
        task3.setId(2);
        task4.setId(3);
        task1.setDuration(timeStep * 2);
        task2.setDuration(timeStep * 2);
        task3.setDuration(timeStep * 2);
        task4.setDuration(timeStep * 2);
        task1.setStartTime(start);
        task2.setStartTime(task1.getEndTime());
        task3.setStartTime(task1.getEndTime()); // будет занято время
        task4.setStartTime(start.withMinute(timeStep)); // будет занято время

        // Проверяем
        assertTrue(timeManager.occupyFor(task1, true), "Время не свободно");
        assertTrue(timeManager.occupyFor(task2, true), "Время не свободно");
        assertFalse(timeManager.occupyFor(task3, true), "Время свободно");
        assertFalse(timeManager.occupyFor(task4, true), "Время свободно");

        // Печать
        long count = timeManager.timeMarks.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                                : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .peek(System.out::println)
                .count();

        System.out.println("count = " +count + "\n");

        long end_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        System.out.printf(startStr);
        System.out.printf("end: %15d, %s\n" , end_0, LocalDateTime.now());
        System.out.println("Заняло: " + ((end_0 - start_0) / 1000.) + " sec\n");
    }


    @Test
    void freeFor() {
        long start_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        String startStr = String.format("start: %7d, %s\n" , start_0, LocalDateTime.now());

        final int timeStep = timeManager.getTimeStep();
        LocalDateTime start = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0, 0);

        Task task1 = new Task("Name1", "Description1", NEW);
        Task task2 = new Task("Name2", "Description2", NEW);
        task1.setId(0);
        task2.setId(1);
        task1.setDuration(timeStep * 2);
        task2.setDuration(timeStep * 3);
        task1.setStartTime(start);
        task2.setStartTime(task1.getEndTime());

        // Проверяем что есть занятые
        assertTrue(timeManager.occupyFor(task1, true), "Время не свободно"); // пока свободно
        assertTrue(timeManager.occupyFor(task2, true), "Время не свободно"); // пока свободно

        long count = timeManager.timeMarks.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                        : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .peek(e -> System.out.println(e.getKey() + " = " + e.getValue()))
                .count();
        System.out.println("count = " +count + "\n");

        assertEquals(5, count); // 5 Занятых ячеек времени (2+3)

        // Проверяем что освободили
        assertTrue(timeManager.freeFor(task1), "Не освободили время");

        count = timeManager.timeMarks.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                        : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .peek(System.out::println)
                .count();
        System.out.println("count = " +count + "\n");

        assertEquals(3, count); // 3 Занятых ячеек времени (2 освободили)

        assertTrue(timeManager.freeFor(task2), "Не освободили время");

        count = timeManager.timeMarks.entrySet().stream()
                .filter(e -> e.getValue() != null)
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
        System.out.println("Заняло: " + ((end_0 - start_0) / 1000.) + " sec\n");
    }



    @Test
    void reset() {
        long start_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        String startStr = String.format("start: %7d, %s\n" , start_0, LocalDateTime.now());

        final int timeStep = timeManager.getTimeStep();
        LocalDateTime start = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0, 0);

        Task task1 = new Task("Name1", "Description1", NEW);
        Task task2 = new Task("Name2", "Description2", NEW);
        task1.setId(0);
        task2.setId(1);
        task1.setDuration(timeStep * 2);
        task2.setDuration(timeStep * 3);
        task1.setStartTime(start);
        task2.setStartTime(task1.getEndTime());

        // Проверяем что есть занятые
        timeManager.occupyFor(task1, true);
        timeManager.occupyFor(task2, true);

        long count = timeManager.timeMarks.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                        : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .peek(e -> System.out.println(e.getKey() + " = " + e.getValue()))
                .count();
        System.out.println("count = " +count + "\n");

        assertEquals(5, count); // 5 Занятых ячеек времени (2+3)

        timeManager.resetMarks();
        count = timeManager.timeMarks.values().stream()
                .filter(Objects::nonNull)
                .count();
        System.out.println("count для значений Objects::nonNull = " +count + "\n");

        assertEquals(0, count, "Количество элементов для значений Objects::nonNull не равно");

        count = timeManager.timeMarks.values().stream()
                .filter(Objects::isNull)
                .count();
        System.out.println("count для значений Objects::isNull = " +count + "\n");

        assertEquals(5, count, "Количество элементов для значений Objects::isNull не равно");

        // Печать
        long end_0 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        System.out.printf(startStr);
        System.out.printf("end: %15d, %s\n" , end_0, LocalDateTime.now());
        System.out.println("Заняло: " + ((end_0 - start_0) / 1000.) + " sec\n");
    }

}