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
import java.time.LocalDateTime;

import static tasks.TaskStatus.*;

public class FileBackedTaskManager extends InMemoryTaskManager{
    final private Path path;
    final private String lineSep = "\n"; // maybe System.lineSeparator()
    public FileBackedTaskManager(Path path) {
        super();
        this.path = path;
    }

    public static void main(String[] args) {
        final String blockSeparator = "-----------";
        Path path = Paths.get("", args);
        FileBackedTaskManager taskManager =  new FileBackedTaskManager(path);

        Task task1 = new Task("Имя_Задачи_1", "Описание_Задачи_1", NEW);
        Task task2 = new Task("Name_Task_2", "Description_Task_2", NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Name_Epic_1", "Description_Epic");
        Epic epic2 = new Epic("Name_Epic_2", "Description_Epic_2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Name_Subtask_1", "Description_Subtask_1", NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Name_Subtask_2", "Description_Subtask_2", NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Name_Subtask_3", "Description_Subtask_3", NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        subtask1.setStatus(IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(DONE);
        taskManager.updateSubtask(subtask2);

        // время
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(20);
        taskManager.updateTask(task1);

        subtask2.setStartTime(LocalDateTime.now().plusMinutes(19));
        subtask2.setDuration(20);
        taskManager.updateSubtask(subtask2);

        subtask1.setStartTime(LocalDateTime.of(2023, 7, 11, 15, 0));
        subtask2.setDuration(0);
        taskManager.updateSubtask(subtask1);

//        System.out.println(task1.getEndTime());
//        System.out.println(subtask2.getStartTime());
//        System.out.println(subtask1.getStartTime());


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

        System.out.println(blockSeparator +"\nИстория просмотра (после вызова всех задач в хаотичном порядке)");
        taskManager.getHistory().forEach(task -> System.out.println("\t" + task));

        System.out.println(blockSeparator +"\nПосле просмотра объектов");
        System.out.println("\tПервый taskManager = " + taskManager.toString().replace("\n", "\n\t"));

        FileBackedTaskManager tm =  loadFromFile(path);
        System.out.println(blockSeparator +"\nПосле создания нового FileBackedTasksManager из файла");
        System.out.println("\tВторой taskManager = " + tm.toString().replace("\n", "\n\t"));

        //Печатаем сортированный список
        System.out.println(blockSeparator +"\nПосле сортировки по времени старта задачи");
        taskManager.getPrioritizedTasks().forEach(task -> System.out.println("\t" + task));
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        final FileBackedTaskManager taskManager =  new FileBackedTaskManager(path);
        try (BufferedReader br = new BufferedReader(new FileReader(path.toString(), StandardCharsets.UTF_8))) {
            boolean nextHasHistory = false;
            int i = -1;
            int maxId = 0;
            // TODO: 03.08.2022 Переделать на for и Files.readString(Path.of(path)); ???
            while (br.ready()) {
                i++;
                String str = br.readLine().trim();
                if (i == 0) continue;
                if (!str.isEmpty()) {
                    if (nextHasHistory) {
                        CSVConverter.fromStringOfHistory(str).forEach(id -> {
                            if (taskManager.getTasksKeeper().containsKey(id)) {
                                taskManager.getHistoryManager().add(taskManager.getTasksKeeper().get(id));
                            } else if (taskManager.getSubtasksKeeper().containsKey(id)) {
                                taskManager.getHistoryManager().add(taskManager.getSubtasksKeeper().get(id));
                            } else if (taskManager.getEpicsKeeper().containsKey(id)) {
                                taskManager.getHistoryManager().add(taskManager.getEpicsKeeper().get(id));
                            }
                        });
                        break;
                    } else {
                        Task task = CSVConverter.fromStringOfTask(str);
                        if (task != null) {
                            int taskId = task.getId();
                            maxId = Math.max(taskId, maxId);
                            switch (task.getType()) {
                                case TASK:
                                    taskManager.getTasksKeeper().put(task.getId(), task);
                                    break;
                                case SUBTASK:
                                    taskManager.getSubtasksKeeper().put(task.getId(), (Subtask) task);
                                    break;
                                case EPIC:
                                    taskManager.getEpicsKeeper().put(task.getId(), (Epic) task);
                                    break;
                            }
                            task.setId(taskId);
                        }
                    }
                } else {
                    nextHasHistory = true;
                }
            }
            taskManager.itemId = maxId;
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
        sb.append(CSVConverter.getHeads()).append(lineSep);
        getTasks().forEach(task -> sb.append(CSVConverter.toString(task)).append(lineSep));
        getEpics().forEach(task -> sb.append(CSVConverter.toString(task)).append(lineSep));
        getSubtasks().forEach(task -> sb.append(CSVConverter.toString(task)).append(lineSep));
        sb.append(lineSep);
        sb.append(CSVConverter.toString(getHistoryManager()));
        try (FileWriter fileWriter = new FileWriter(path.toString(), StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи");
        }
    }

    @Override
    public int addTask(Task task) {
        int id  = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return  id;
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
