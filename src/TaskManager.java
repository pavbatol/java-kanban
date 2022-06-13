import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    /**
     * +++2.1 Получение списка всех задач
     */

    // +++Получение для Task
    ArrayList<Task> getTasks();

    // +++Получение для Subtask
    ArrayList<Subtask> getSubtasks();

    // +++Получение для Epic
    ArrayList<Epic> getEpics();

    /**
     * +++2.2 Удаление всех задач.
     */

    // +++Удаление для Task
    void removeTasks();

    // +++Удаление для Subtask
    void removeSubtasks();

    // +++Удаление для Epic
    void removeEpics();

    /**
     * +++2.3 Получение по идентификатору.
     */

    // +++Получение для Task
    Task getTaskById(int id) ;

    // +++Получение для Subtask
    Subtask getSubtaskById(int id);

    // +++Получение для Epic
    Epic getEpicById(int id);

    /**
     * +++2.4 Создание. Сам объект должен передаваться в качестве параметра.
     */

    // TODO: 13.06.2022 Можно перегрузить метод
    // +++Создание для Task
    void addTask(Task task);

    // +++Создание для Subtask
    void addSubtask(Subtask subtask);

    // +++Создание для Epic
    void addEpic(Epic epic);

    /**
     * +++2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
     */

    // TODO: 13.06.2022 Можно перегрузить метод
    // +++Обновление для Task
    void updateTask(Task task);

    // +++Обновление для Subtask
    void updateSubtask(Subtask subtask);

    // +++Обновление для Epic
    void updateEpic(Epic epic);

    /**
     * +++2.6 Удаление по идентификатору.
     */

    // +++Удаление для Task
    void removeTaskById(int id);

    // +++Удаление для Subtask
    void removeSubtaskById(int id);

    // +++Удаление для Epic
    void removeEpicById(int id);

    /**
     * +++3.1 Получение списка всех подзадач определённого эпика.
     */

    ArrayList<Subtask> getSubtasksByEpic(int epicId);

    /**
     * Возвращать последние 10 просмотренных задач
     */

    List<Task> getHistory();
}
