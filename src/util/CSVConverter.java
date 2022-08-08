package util;

import managers.HistoryManager;
import tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static util.Functions.isPositiveInt;

public final class CSVConverter {
    private CSVConverter() {
    }

    public static String toString(Task task) {
        if (task == null) {
            System.out.println("Перевод в строку НЕ выполнен, объект не инициализирован");
            return null;
        }
        //Собираем: id,type,name,status,description,duration,startTime,endTime,relations
        TaskType type = task.getType();
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId())
                .append(",").append(type.name())
                .append(",").append(task.getName())
                .append(",").append(task.getStatus())
                .append(",").append(task.getDescription())
                .append(",").append(task.getDuration())
                .append(",").append(task.getStartTime() != null ? task.getStartTime().toString() : null);
        switch (type) {
            case TASK:
                sb.append(",").append("null").append(",");
                break;
            case SUBTASK:
                sb.append(",").append("null").append(",").append(((Subtask) task).getEpicId());
                break;
            case EPIC:
                sb.append(",").append(task.getEndTime() != null ? task.getEndTime().toString() : null);
                if (((Epic) task).getSubtaskIds().size() == 0) {
                    sb.append(",");
                } else {
                    ((Epic) task).getSubtaskIds().forEach(subtaskId -> sb.append(",").append(subtaskId));
                }
                break;
        }
        return sb.toString();
    }

    public static String toString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        manager.getHistory().forEach(task -> sb.append(task.getId()).append(","));
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1); // последнюю запятую
        }
        return sb.toString();
    }

    public static Task fromStringOfTask(String value) {
        if (value == null) {
            System.out.println("Перевод строки в задачу НЕ выполнен, строка = null");
            return null;
        }
        int minNumberOfDataInLine = 7; // Минимальное количество данных в строке
        int relationsIndex = minNumberOfDataInLine + 1; // Индекс, в котором связи
        String[] parts = value.split(",");
        if (parts.length < minNumberOfDataInLine) {
            System.out.println("Перевод в задачу НЕ выполнен, мало данных в строке: " + Arrays.toString(parts));
            return null;
        }
        String name = parts[2].trim();
        String description = parts[4].trim();
        int id;
        TaskType type;
        TaskStatus status;
        int duration;
        LocalDateTime startTime;
        Task task = null;
        try {
            id = Integer.parseInt(parts[0].trim());
            type = TaskType.valueOf(parts[1].trim());
            status = TaskStatus.valueOf(parts[3].trim());
            duration = Integer.parseInt(parts[5].trim());
            startTime = parts[6].trim().equals("null") ? null : LocalDateTime.parse(parts[6].trim());
        } catch (IllegalArgumentException e) {
            System.out.println("Перевод строки в задачу НЕ выполнен, некорректные данные: " + value);
            return null;
        }
        switch (type) {
            case TASK:
                task = new Task(name, description,status);
                break;
            case SUBTASK:
                if (parts.length > relationsIndex  && isPositiveInt(parts[relationsIndex])) {
                    int epicId = Integer.parseInt(parts[relationsIndex]);
                    task = new Subtask(name, description, status, epicId); //Задачу не создаем если нет к какому Эпику привязан
                }
                break;
            case EPIC:
                task = new Epic(name, description);
                LocalDateTime endTime = !parts[7].trim().equals("null") ? LocalDateTime.parse(parts[6].trim()) : null;
                ((Epic)task).setEndTime(endTime);
                for (int i = relationsIndex; i < parts.length; i++) {
                    if (isPositiveInt(parts[i].trim())) {
                        ((Epic) task).addSubtaskById(Integer.parseInt(parts[i].trim()));
                    } else {
                        task = null; // Задачу с некорректными данными не будем создавать
                        break;
                    }
                }
                break;
        }
        if (task != null) {
            task.setId(id);
            task.setStatus(status); // TODO: 04.08.2022 Статус только требуется для Эпика
            task.setDuration(duration);
            task.setStartTime(startTime);
        }
        return task;
    }

    public static List<Integer> fromStringOfHistory(String value) {
        if (value != null) {
            return Arrays.stream(value.split(","))
                    .filter(part -> isPositiveInt(part.trim()))
                    .map(part -> Integer.parseInt(part.trim()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static String getHeads() {
        return "id," +
                "type," +
                "name," +
                "status," +
                "description," +
                "duration," +
                "startTime," +
                "endTime," + // only for Epic, otherwise "null"
                "relations"; // relations = epic <-> subtask
    }



}
