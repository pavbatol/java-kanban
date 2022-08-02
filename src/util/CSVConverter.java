package util;

import managers.HistoryManager;
import tasks.*;

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
        //Определим тип задачи и Собираем: id,type,name,status,description,relations
        TaskType type = task.getType();
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId())
                .append(",").append(type.name())
                .append(",").append(task.getName())
                .append(",").append(task.getStatus())
                .append(",").append(task.getDescription());
        switch (type) {
            case TASK:
                sb.append(",");
                break;
            case SUBTASK:
                sb.append(",").append(((Subtask) task).getEpicId());
                break;
            case EPIC:
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
            System.out.println("Перевод строки в задачу НЕ выполнен, строка не инициализирована");
            return null;
        }
        int minNumberOfDataInLine = 5; // Минимальное количество данных в строке
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
        Task task = null;
        try {
            id = Integer.parseInt(parts[0].trim());
            type = TaskType.valueOf(parts[1].trim());
            status = TaskStatus.valueOf(parts[3].trim());
        } catch (IllegalArgumentException e) {
            System.out.println("Перевод строки в задачу НЕ выполнен, некорректные данные: " + value);
            return null;
        }
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case SUBTASK:
                if (parts.length > minNumberOfDataInLine && isPositiveInt(parts[minNumberOfDataInLine])) {
                    int epicId = Integer.parseInt(parts[minNumberOfDataInLine]);
                    task = new Subtask(name, description, epicId); // Задачу не создаем если нет к какому Эпику привязан
                }
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
        }
        if (task != null) {
            task.setId(id);
            task.setStatus(status);
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
        return "id,type,name,status,description,relations"; //relations = epic
    }



}
