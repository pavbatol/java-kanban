import java.util.ArrayList;

public interface TaskManager {
    /**
     * +++2.1 Получение списка всех задач
     */

    // +++Получение для Task
    public ArrayList<Task> getTasks();

    // +++Получение для Subtask
    public ArrayList<Subtask> getSubtasks();

    // +++Получение для Epic
    public ArrayList<Epic> getEpics();

    /**
     * +++2.2 Удаление всех задач.
     */

    // +++Удаление для Task
    public void removeTasks();

    // +++Удаление для Subtask
    public void removeSubtasks();

    // +++Удаление для Epic
    public void removeEpics();

    /**
     * +++2.3 Получение по идентификатору.
     */

    // +++Получение для Task
    public Task getTaskById(int id) ;

    // +++Получение для Subtask
    public Subtask getSubtaskById(int id);

    // +++Получение для Epic
    public Epic getEpicById(int id);

    /**
     * +++2.4 Создание. Сам объект должен передаваться в качестве параметра.
     */

    // TODO: 13.06.2022 Можно перегрузить метод
    // +++Создание для Task
    public void addTask(Task task);

    // +++Создание для Subtask
    public void addSubtask(Subtask subtask);

    // +++Создание для Epic
    public void addEpic(Epic epic);

    /**
     * +++2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
     */

    // TODO: 13.06.2022 Можно перегрузить метод
    // +++Обновление для Task
    public void updateTask(Task task);

    // +++Обновление для Subtask
    public void updateSubtask(Subtask subtask);

    // +++Обновление для Epic
    public void updateEpic(Epic epic);

    /**
     * +++2.6 Удаление по идентификатору.
     */

    // +++Удаление для Task
    public void removeTaskById(int id);

    // +++Удаление для Subtask
    public void removeSubtaskById(int id);

    // +++Удаление для Epic
    public void removeEpicById(int id);

    /**
     * +++3.1 Получение списка всех подзадач определённого эпика.
     */

    public ArrayList<Subtask> getSubtasksByEpic(int epicId);



}
