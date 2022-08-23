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

    // TODO: 21.08.2022 Учесть userId
    public static String toString(Task task) {
        if (task == null) {
            System.out.println("Перевод в строку НЕ выполнен, объект не инициализирован");
            return null;
        }
        //Собираем: id,type,name,status,description,duration,startTime,endTime,relations
        TaskType type = task.getType();
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId())
                .append(",").append(task.getUserId())
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

    // TODO: 21.08.2022 Учесть userId
    public static Task fromStringOfTask(String value) {
        if (value == null) {
            System.out.println("Перевод строки в задачу НЕ выполнен, строка = null");
            return null;
        }
        int minNumberOfDataInLine = 8; // Минимальное количество данных в строке
        int relationsIndex = minNumberOfDataInLine + 1; // Индекс, в котором связи
        String[] parts = value.split(",");
        if (parts.length < minNumberOfDataInLine) {
            System.out.println("Перевод в задачу НЕ выполнен, мало данных в строке: " + Arrays.toString(parts));
            return null;
        }
        String name = parts[2 + 1].trim();
        String description = parts[4 + 1].trim();
        int id;
        int userId;
        TaskType type;
        TaskStatus status;
        int duration;
        LocalDateTime startTime;
        Task task = null;
        try {
            id = Integer.parseInt(parts[0].trim());
            userId = Integer.parseInt(parts[0 + 1].trim());
            type = TaskType.valueOf(parts[1 + 1].trim());
            status = TaskStatus.valueOf(parts[3 + 1].trim());
            duration = Integer.parseInt(parts[5 + 1].trim());
            startTime = parts[6 + 1].trim().equals("null") ? null : LocalDateTime.parse(parts[6 + 1].trim());
        } catch (IllegalArgumentException e) {
            System.out.println("Перевод строки в задачу НЕ выполнен, некорректные данные: " + value);
            return null;
        }
        switch (type) {
            case TASK:
                task = new Task(id, userId, name, description, status, duration, startTime);
                break;
            case SUBTASK:
                if (parts.length > relationsIndex  && isPositiveInt(parts[relationsIndex])) { //Без Эпика не создаем
                    int epicId = Integer.parseInt(parts[relationsIndex]);
                    task = new Subtask(id, userId, name, description, status, duration, startTime, epicId);
                }
                break;
            case EPIC:
                if (parts.length > minNumberOfDataInLine) {
                    task = new Epic(id, userId, name, description, status);
                    LocalDateTime endTime = !parts[minNumberOfDataInLine].trim().equals("null")
                            ? LocalDateTime.parse(parts[6].trim()) : null;
                    ((Epic) task).setEndTime(endTime);
                    for (int i = relationsIndex; i < parts.length; i++) {
                        if (isPositiveInt(parts[i].trim())) {
                            ((Epic) task).addSubtaskById(Integer.parseInt(parts[i].trim()));
                        } else {
                            task = null; // Задачу с некорректными данными не создаем
                            break;
                        }
                    }
                }
                break;
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

    // TODO: 21.08.2022 Учесть userId
    public static String getHeads() {
        return "id," +
                "userId," +
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
