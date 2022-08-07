package managers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Schedule {
    int timeStep; // Шаг изменения времени задачи
    Map<String, Boolean> occupiedTimes;

    public Schedule(int timeStep) {
        this.timeStep = getCorrectTimeStep(timeStep);
        occupiedTimes = getNewOccupiedTimes();
//        int capacity = getCapacity(this.timeStep);
//        occupiedTimes = new HashMap<>(capacity);
//        occupiedTimes = Stream.iterate(1, i -> i <= capacity, i -> i + 1)
//                //.limit(20)
//                .map(i -> LocalDateTime.of(
//                            LocalDate.now().getYear(),
//                            LocalDate.now().getMonth(),
//                            LocalDate.now().getDayOfMonth(),
//                            0,
//                            0).plusMinutes((long) this.timeStep * (i - 1)))
//                .map(e -> new String[] {e.toString(), "false"})
//                .collect(Collectors.toMap(e -> (String)e[0], e -> Boolean.getBoolean(e[1])));
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
}

