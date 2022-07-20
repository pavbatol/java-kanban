package managers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static util.Functions.getTaskType;
import static util.Functions.isPositiveInt;

public class FileBackedTasksManager extends InMemoryTaskManager{
    final private Path path;
    final private int minNumberOfDataInLine;
    public FileBackedTasksManager(Path path) {
        super();
        this.path = path;
        this.minNumberOfDataInLine = 5; // Минимальное количество данных в строке
    }

    public static void main(Path path) {
        final String lineSeparator = "-----------";
        FileBackedTasksManager taskManager =  new FileBackedTasksManager(path);

        Task task1 = new Task("Name_Task_1", "Description_Task_1");
        Task task2 = new Task("Name_Task_2", "Description_Task_2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Name_Epic_1", "Description_Epic");
        Epic epic2 = new Epic("Name_Epic_2", "Description_Epic_2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Name_Subtask_1", "Description_Subtask_1", epic1.getId());
        Subtask subtask2 = new Subtask("Name_Subtask_2", "Description_Subtask_2", epic1.getId());
        Subtask subtask3 = new Subtask("Name_Subtask_3", "Description_Subtask_3", epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        System.out.println("После создания объектов");
        System.out.println("\tПервый taskManager = " + taskManager.toString().replace("\n", "\n\t"));

        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task1.getId());
        System.out.println(lineSeparator +"\nИстория просмотра (после вызова всех задач в хаотичном порядке)");
        taskManager.getHistory().forEach(task -> System.out.println("\t" + task));

        System.out.println(lineSeparator +"\nПосле просмотра объектов");
        System.out.println("\tПервый taskManager = " + taskManager.toString().replace("\n", "\n\t"));

        FileBackedTasksManager tm =  loadFromFile(path);
        System.out.println(lineSeparator +"\nПосле создания нового FileBackedTasksManager из файла");
        System.out.println("\tВторой taskManager = " + tm.toString().replace("\n", "\n\t"));
    }

    public static String toString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        manager.getHistory().forEach(task -> sb.append(task.getId()).append(","));
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1); // последнюю запятую
        }
        return sb.toString();
    }

    public static List<Integer> fromStringHistory(String value) {
        List<Integer> result = new ArrayList<>();
        if (value != null) {
            for (String part : value.split(",")) {
                part = part.trim();
                if (isPositiveInt(part)) {
                    result.add(Integer.parseInt(part));
                }
            }
        }
        return result;
    }

    // TODO: 20.07.2022 Еще обновить надо itemId (Создавать задачи через метод добавления)
    public static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager taskManager =  new FileBackedTasksManager(path);
        try (FileReader reader = new FileReader(path.toString(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {
            boolean nextHasHistory = false;
            while (br.ready()) {
                String str = br.readLine().trim();
                if (!str.isEmpty()) { // TODO: 20.07.2022 Наполнять историю через вызов просмотра задач по id
                    if (nextHasHistory) {
                        fromStringHistory(str).forEach(id -> {
                            if (taskManager.tasks.containsKey(id)) {
                                taskManager.getHistoryManager().add(taskManager.tasks.get(id));
                            } else if (taskManager.subtasks.containsKey(id)) {
                                taskManager.getHistoryManager().add(taskManager.subtasks.get(id));
                            } else if (taskManager.epics.containsKey(id)) {
                                taskManager.getHistoryManager().add(taskManager.epics.get(id));
                            }
                        });
                    } else { // TODO: 20.07.2022 Создавать задачи через метод добавления и обновлять нужные поля
                        Task task = taskManager.fromStringTask(str);
                        if (task != null) {
                            TaskType taskType = getTaskType(task);
                            switch (taskType) {
                                case TASK:
                                    taskManager.tasks.put(task.getId(), task);
                                    break;
                                case SUBTASK:
                                    taskManager.subtasks.put(task.getId(), (Subtask) task);
                                    break;
                                case EPIC:
                                    taskManager.epics.put(task.getId(), (Epic) task);
                                    break;
                            }
                        }
                    }
                } else {
                    nextHasHistory = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения.");
        }
        return taskManager;
    }

    private void save() throws ManagerSaveException {
        // Создаем директории
        if (!Files.exists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания директории");
            }
        }
        // Составляем строку
        StringBuilder sb = new StringBuilder();
        getTasks().forEach(task -> sb.append(toString(task)).append("\n"));
        getEpics().forEach(task -> sb.append(toString(task)).append("\n"));
        getSubtasks().forEach(task -> sb.append(toString(task)).append("\n"));
        sb.append("\n");
        sb.append(toString(getHistoryManager()));
        // Записываем в файл
        try (FileWriter fileWriter = new FileWriter(path.toString(), StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи");
        }
    }

    private String toString(Task task) {
        if (task == null) {
            System.out.println("Перевод в строку НЕ выполнен, объект не инициализирован");
            return null;
        }
        //Определим тип задачи и Собираем: id,type,name,status,description,relations
        TaskType type = getTaskType(task);
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).
                append(",").append(type.name()).
                append(",").append(task.getName()).
                append(",").append(task.getStatus()).
                append(",").append(task.getDescription());
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
                } else { // TODO: 20.07.2022 Не надо, если создавать задачи через метод добавления
                    ((Epic) task).getSubtaskIds().forEach(subtaskId -> sb.append(",").append(subtaskId));
                }
                break;
        }
        return sb.toString();
    }

    private Task fromStringTask(String value) {
        if (value == null) {
            System.out.println("Перевод строки в задачу НЕ выполнен, строка не инициализирована");
            return null;
        }
        String[] parts = value.split(",");
        if (parts.length < minNumberOfDataInLine) {
            System.out.println("Перевод в задачу НЕ выполнен, мало данных в строке: " + Arrays.toString(parts));
            return null;
        }
        // Воссоздание задачи
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
            System.out.println("Перевод строки в задачу НЕ выполнен, некорректные данные");
            return null;
        }
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case SUBTASK:
                if (parts.length > 5 && isPositiveInt(parts[5])) {
                    int epicId = Integer.parseInt(parts[5]);
                    task = new Subtask(name, description, epicId); // Задачу не создаем если нет к какому Эпику привязан
                }
                break;
            case EPIC:
                task = new Epic(name, description);
                for (int i = 5; i < parts.length; i++) {
                    if (isPositiveInt(parts[i].trim())) {
                        ((Epic) task).addSubtaskById(Integer.parseInt(parts[i].trim()));
                    } else {
                        task = null; // Задачу с некорректными данными не будем создавать
                        break;
                    }
                }
        }
        if (task != null) {
            task.setId(id);
            task.setStatus(status);
        }
        return task;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
