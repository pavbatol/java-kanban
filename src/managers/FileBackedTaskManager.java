package managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import util.CSVConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static util.Functions.getAnyTypeTaskById;

public class FileBackedTaskManager extends InMemoryTaskManager{
    final private Path path;
    public FileBackedTaskManager(Path path) {
        super();
        this.path = path;
    }

    public static void main(String[] args) {
        final String lineSeparator = "-----------";
        Path path = Paths.get("", args);
        FileBackedTaskManager taskManager =  new FileBackedTaskManager(path);

        Task task1 = new Task("Имя_Задачи_1", "Описание_Задачи_1");
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
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
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

        FileBackedTaskManager tm =  loadFromFile(path);
        System.out.println(lineSeparator +"\nПосле создания нового FileBackedTasksManager из файла");
        System.out.println("\tВторой taskManager = " + tm.toString().replace("\n", "\n\t"));
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        final FileBackedTaskManager taskManager =  new FileBackedTaskManager(path);
        try (BufferedReader br = new BufferedReader(new FileReader(path.toString(), StandardCharsets.UTF_8))) {
            boolean nextHasHistory = false;
            int i = -1;
            while (br.ready()) {
                i++;
                String str = br.readLine().trim();
                if (i == 0) continue;
                if (!str.isEmpty()) {
                    if (nextHasHistory) {
                        CSVConverter.fromStringOfHistory(str).forEach(id -> getAnyTypeTaskById(id,taskManager));
                        break;
                    } else {
                        Task task = CSVConverter.fromStringOfTask(str);
                        if (task != null) {
                            int taskId = task.getId();
                            switch (task.getType()) {
                                case TASK:
                                    taskManager.addTask(task);
                                    break;
                                case SUBTASK:
                                    taskManager.addSubtask((Subtask) task);
                                    break;
                                case EPIC:
                                    taskManager.addEpic((Epic) task);
                                    break;
                            }
                            task.setId(taskId);
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
        if (!Files.exists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания директорий");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(CSVConverter.getHeads()).append("\n");
        getTasks().forEach(task -> sb.append(CSVConverter.toString(task)).append("\n"));
        getEpics().forEach(task -> sb.append(CSVConverter.toString(task)).append("\n"));
        getSubtasks().forEach(task -> sb.append(CSVConverter.toString(task)).append("\n"));
        sb.append("\n");
        sb.append(CSVConverter.toString(getHistoryManager()));
        try (FileWriter fileWriter = new FileWriter(path.toString(), StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи");
        }
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
