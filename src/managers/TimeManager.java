package managers;

import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TimeManager {
    protected final int timeStep;
    protected final Map<String, Task> timeMarks;

    public TimeManager(int timeStep) {
        this.timeStep = getCorrectTimeStep(timeStep);
        timeMarks = new HashMap<>();
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

    private boolean isCorrectDates(LocalDateTime start, LocalDateTime end) {
        String message = getClass().getSimpleName() + ": ";
        if (start == null || end == null) {
            System.out.println(message + "Получен null для start или end");
            return false;
        }
        if (start.isAfter(end) || start.isEqual(end)) {
            System.out.println(message + "Неверное порядок для start и end");
            return false;
        }
        if (start.getMinute() % timeStep != 0 || end.getMinute() % timeStep != 0) {
            System.out.println(message + "Не совпадает шаг времени для start или end");
            return false;
        }
        return true;
    }

    public boolean isFreeFor(Task task) {
        if (task == null) {
            return false;
        }
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        if (!isCorrectDates(start, end)) {
            return false;
        }
        Duration duration = Duration.between(start, end);
        final long count = duration.getSeconds() / 60 / timeStep;
        for (int i = 0; i < count; i++) {
            String key = start.plusMinutes((long) timeStep * i ).toString();
            Task otherTask = timeMarks.getOrDefault(key, null);
            if (otherTask != null && !otherTask.equals(task)) {
                return false;
            }
        }
        return true;
    }

    public boolean occupyFor(Task task , boolean checkForFree) {
        if (task == null) {
            return false;
        }
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        if (!isCorrectDates(start, end)) {
            return false;
        }
        if (checkForFree && !isFreeFor(task)) {
            return false;
        }
        // обозначаем занятость
        Duration duration = Duration.between(start, end);
        final long count = duration.getSeconds() / 60 / timeStep;
        for (int i = 0; i < count; i++) {
            String key = start.plusMinutes((long) timeStep * i).toString();
            timeMarks.put(key, task);
        }
        return true;
    }

    public boolean freeFor(Task task) {
        if (task == null) {
            return false;
        }
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        if (!isCorrectDates(start, end)) {
            return false;
        }
        // освобождаем занятость
        Duration duration = Duration.between(start, end);
        final long count = duration.getSeconds() / 60 / timeStep;
        for (int i = 0; i < count; i++) {
            String key = start.plusMinutes((long) timeStep * i).toString();
            timeMarks.remove(key);
        }
        return true;
    }

    public void clearMarks() {
        //timeMarks.forEach((k, v) -> timeMarks.put(k, null));
        timeMarks.clear();
    }

    public int getTimeStep() {
        return timeStep;
    }

    public Map<String, Integer> getTimeMarksInt() {
        return timeMarks.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        (entry) -> entry.getValue().getId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeManager that = (TimeManager) o;
        return timeStep == that.timeStep && timeMarks.equals(that.timeMarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStep, timeMarks);
    }

    @Override
    public String toString() {
        List<String> timeMarksStrs = getTimeMarksInt().entrySet().stream()
                .sorted((o1, o2) -> LocalDateTime.parse(o1.getKey()).isAfter(LocalDateTime.parse(o2.getKey())) ? 1
                        : LocalDateTime.parse(o1.getKey()).isBefore(LocalDateTime.parse(o2.getKey())) ? -1 : 0)
                .map(e -> "\n\t\t" + e.getKey() + " = " +  e.getValue())
                .collect(Collectors.toList());

        return "TimeManager{" +
                "\n\ttimeStep=" + timeStep +
                "\n\ttimeMarks=" + timeMarksStrs + "\n" +
                '}';

    }
}

