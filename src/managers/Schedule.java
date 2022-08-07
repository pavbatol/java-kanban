package managers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Schedule {
    protected int timeStep; // Шаг изменения времени задачи
    protected Map<String, Boolean> occupiedTimes;

    public Schedule(int timeStep) {
        this.timeStep = getCorrectTimeStep(timeStep);
        occupiedTimes = getNewOccupiedTimes();
    }

    private int getCorrectTimeStep(int timeStep) {
        if (timeStep <= 1) {
            timeStep = 1;
        } else if (timeStep < 6) {
            timeStep = 5;
        } else if (timeStep < 11) {
            timeStep = 10;
        } else {
            timeStep = 15;
        }
        return timeStep;
    }

    private int getCapacity() {
        if (this.timeStep < 0) {
            System.out.println("Неверный timeStep: " + timeStep);
            return 0;
        }
        return 60 / this.timeStep * 24 * 365;
    }

    private Map<String, Boolean> getNewOccupiedTimes() {
        int capacity = getCapacity();
        return Stream.iterate(1, i -> i <= capacity, i -> i + 1)
                //.limit(20)
                .map(i -> LocalDateTime.of(
                        LocalDate.now().getYear(),
                        LocalDate.now().getMonth(),
                        LocalDate.now().getDayOfMonth(),
                        0,
                        0).plusMinutes((long) this.timeStep * (i - 1)))
                .map(e -> new String[] {e.toString(), "false"})
                .collect(Collectors.toMap(e -> (String)e[0], e -> Boolean.getBoolean(e[1])));
    }

    protected boolean occupy(LocalDateTime start, LocalDateTime end) {
        if (start.getMinute() % timeStep != 0 || end.getMinute() % timeStep != 0) {
            System.out.println("Не совпадает шаг времени");
            return false;
        }
        if (!occupiedTimes.containsKey(start.toString()) || !occupiedTimes.containsKey(end.toString())) {
            System.out.println("Время за границами 1-го года");
            return false;
        }

        Duration duration = Duration.between(start, end);
        if (duration.getSeconds() % 60 != 0) {
            System.out.println("Не совпадает шаг времени через секунды");
            return false;
        }

        final long count = duration.getSeconds() / 60 / timeStep;
        long filteredCount = Stream.iterate(1, i -> i <= count, i -> i + 1)
                .map(i -> start.plusMinutes((long) this.timeStep * (i - 1)))
                .filter(ldt -> occupiedTimes.containsKey(ldt.toString()))
                .filter(ltd -> !occupiedTimes.get(ltd.toString()))
                .count();

        if (filteredCount == count) {
            // обозначаем занятость
            long setedCount = Stream.iterate(1, i -> i <= count, i -> i + 1)
                    .map(i -> start.plusMinutes((long) this.timeStep * (i - 1)))
                    .map(LocalDateTime::toString)
                    .filter(s -> occupiedTimes.containsKey(s))
                    .peek(s -> occupiedTimes.put(s, true))
                    .count();
            return true;
        }
        return false;
    }

    protected void free(LocalDateTime start, LocalDateTime end) {

    }

    protected int getTimeStep() {
        return this.timeStep;
    }

    public Map<String, Boolean> getOccupiedTimes() {
        return this.occupiedTimes;
    }
}

