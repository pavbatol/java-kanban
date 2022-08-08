package managers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeManager {
    protected final int timeStep; // Шаг изменения времени задачи
    protected final Map<String, Boolean> times; //k->Время строкой, v->Занято или нет

    public TimeManager(int timeStep) {
        this.timeStep = getCorrectTimeStep(timeStep);
        times = getNewTimes();
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

    private Map<String, Boolean> getNewTimes() {
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
                .collect(Collectors.toMap(e -> e[0], e -> Boolean.getBoolean(e[1])));
    }

    private Duration getBetween(LocalDateTime start, LocalDateTime end) {
        if (!isCorrectStartAndEnd(start, end)) {
            return null;
        }
        Duration duration = Duration.between(start, end);
        if ((duration.getSeconds() / 60) % timeStep != 0) {
            System.out.println(getClass().getSimpleName() + ": Не совпадает шаг времени при переводе в минуты из секунд");
            return null;
        }
        return duration;
    }

    private boolean isCorrectStartAndEnd(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            System.out.println(getClass().getSimpleName() + ": Получен null для start или end");
            return false;
        }
        if (start.isAfter(end) || start.isEqual(end)) {
            System.out.println(getClass().getSimpleName() + ": Неверное порядок для start и end");
            return false;
        }
        if (start.getMinute() % timeStep != 0 || end.getMinute() % timeStep != 0) {
            System.out.println(getClass().getSimpleName() + ": Не совпадает шаг времени для start или end");
            return false;
        }
        if (!times.containsKey(start.toString()) || !times.containsKey(end.toString())) {
            System.out.println(getClass().getSimpleName() + ": Время за границами одного года для start или end");
            return false;
        }
        return true;
    }

    public boolean isFree(LocalDateTime start, LocalDateTime end) {
        Duration duration = getBetween(start, end);
        if (duration == null) {
            return false;
        }
        final long count = duration.getSeconds() / 60 / timeStep;
        long filteredCount = Stream.iterate(1, i -> i <= count, i -> i + 1)
                .map(i -> start.plusMinutes((long) this.timeStep * (i - 1)))
                .filter(ldt -> times.containsKey(ldt.toString()))
                .filter(ltd -> !times.get(ltd.toString()))
                .count();
        return filteredCount == count;
    }

    public boolean occupy(LocalDateTime start, LocalDateTime end, boolean checkForFree) {
        if (checkForFree && !isFree(start, end)) {
            return false;
        }
        Duration duration = getBetween(start, end);
        if (duration != null) {
            // обозначаем занятость
            final long count = duration.getSeconds() / 60 / timeStep;
            long setCount = Stream.iterate(1, i -> i <= count, i -> i + 1)
                    .map(i -> start.plusMinutes((long) this.timeStep * (i - 1)))
                    .map(LocalDateTime::toString)
                    .filter(times::containsKey)
                    .peek(s -> times.put(s, true))
                    .count();
            return setCount == count;
        }
        return false;
    }

    public boolean free(LocalDateTime start, LocalDateTime end) {
        Duration duration = getBetween(start, end);
        if (duration != null) {
            // освобождаем занятость
            final long count = duration.getSeconds() / 60 / timeStep;
            long setCount = Stream.iterate(1, i -> i <= count, i -> i + 1)
                    .map(i -> start.plusMinutes((long) this.timeStep * (i - 1)))
                    .map(LocalDateTime::toString)
                    .filter(times::containsKey)
                    .peek(s -> times.put(s, false))
                    .count();
            return setCount == count;
        }
        return false;
    }

    public void reset() {
        times.keySet().stream()
                .filter(times::containsKey)
                .peek((k) -> times.put(k, false))
                .count();
    }

    public int getTimeStep() {
        return timeStep;
    }
}

