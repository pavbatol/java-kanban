package managers;

import tasks.*;

import java.io.File;

import static tasks.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager{
    final File fileBacked;
    final int minNumberOfDataInLine = 6;
    public FileBackedTasksManager(File fileBacked) {
        super();
        this.fileBacked = fileBacked;
    }

//    static void main(String[] args) {
//
//    }
//
//    static FileBackedTasksManager loadFromFile(File file) {
//
//
//    }
//
//    static String toString(HistoryManager manager) {
//
//    }
//
//    static List<Integer> fromString(String value) {
//        List<Integer> result = new ArrayList<>();
//
//        return result;
//    }

    private String toString(Task task) {
        if (task == null) {
            System.out.println("Перевод в строку НЕ выполнен, объект не инициализирован");
            return null;
        }
        //Определим тип задачи
        TaskType type = TASK;
        if (task.getClass() == Subtask.class) {
            type = SUBTASK;
        } else if (task.getClass() == Epic.class) {
            type = EPIC;
        }
        // Собираем: id,type,name,status,description,relations
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).
                append(",").append(type.name()).
                append(",").append(task.getName()).
                append(",").append(task.getStatus()).
                append(",").append(task.getDescription());
        switch (type) {
            case TASK:
                sb.append(",").append("-1"); // для Task эти данные не используются
                break;
            case SUBTASK:
                sb.append(",").append(((Subtask) task).getEpicId());
                break;
            case EPIC:
                ((Epic) task).getSubtaskIds().forEach(subtaskId -> sb.append(",").append(subtaskId));
                break;
            default:
                break;
        }
        return sb.toString();
    }

    private Task fromString(String value) {  // TODO: 18.07.2022 Сделать короче метод
        if (value == null) {
            System.out.println("Перевод строки в задачу НЕ выполнен, строка не инициализирована");
            return null;
        }
        String[] parts = value.split(",");
        if (parts.length < minNumberOfDataInLine) {
            System.out.println("Перевод строки в задачу НЕ выполнен, мало данных в строке");
            return null;
        }
        // Создание задачи
        Task task = null;
        int id;
        TaskType type;
        String name = parts[2].trim();
        TaskStatus status;
        String description = parts[4].trim();
        try {
            type = TaskType.valueOf(parts[1].trim());
            status = TaskStatus.valueOf(parts[3].trim());
            id = Integer.parseInt(parts[0].trim());
        } catch (IllegalArgumentException e) {
            System.out.println("Перевод строки в задачу НЕ выполнен, некорректные данные");
            return null;
        }
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case SUBTASK:
                if (isInt(parts[5])) {
                    int epicId = Integer.parseInt(parts[5]);
                    task = new Subtask(name, description, epicId);
                }
                break;
            case EPIC:
                task = new Epic(name, description);
                for (int i = 5; i < parts.length; i++) {
                    if (isInt(parts[i])) {
                        ((Epic) task).addSubtaskById(Integer.parseInt(parts[i]));
                    } else {
                        task = null; // Задачу с кривыми данными не будем создавать
                        break;
                    }
                }
            default:
        }
        if (task != null) {
            task.setId(id);
            task.setStatus(status);
        }
        return task;
    }

    public void save() {

    }

    public boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
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
    public String toString() {
        return super.toString();
    }
}
