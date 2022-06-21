package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // Получение списка всех задач
    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    // Удаление всех задач.
    void removeTasks();

    void removeSubtasks();

    void removeEpics();

    // Получение по идентификатору.
    Task getTaskById(int id) ;

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    //Добавить задачу
    // TODO: 13.06.2022 Можно перегрузить метод
    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    //Обновить задачу
    // TODO: 13.06.2022 Можно перегрузить метод
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    // Удаление по идентификатору.
    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    // Получение списка всех подзадач определённого эпика.
    List<Subtask> getSubtasksByEpicId(int epicId);

    // Возвращать последние 10 просмотренных задач
    List<Task> getHistory();
}
