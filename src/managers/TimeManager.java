package managers;

import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TimeManager {
    protected final int timeStep; // Шаг изменения времени задачи
    protected final Map<String, Task> timeMarks; //k->Время строкой, v->Занято или нет

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
}

