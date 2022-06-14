import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        id = -1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    // Синхронизировать статус у Эпика
    private void synchronizeEpicStatus(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Статусы НЕ синхронизированы, id не найден");
            return;
        }
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Статусы НЕ синхронизированы, объект не инициализирован");
            return;
        }
        // Проверяем статусы у всех подзадач Эпика
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        int doneStatusCount = 0; // считать DON
        int newStatusCount = 0; // считать NEW

        boolean isBreak = false;
        for (int subtaskId : subtaskIds) {
            if (isBreak) {
                break;
            }
            if (!subtasks.containsKey(subtaskId) || subtasks.get(subtaskId) == null) {
                continue;
            }
            // Получаем статус каждой подзадачи
            TaskStatus subtaskStatus = subtasks.get(subtaskId).getStatus();
            if (subtaskStatus == null) {
                continue;
            }
            switch (subtaskStatus) {
                case IN_PROGRESS:
                    isBreak = true; // можно прервать цикл, уже все ясно
                    break;
                case DONE:
                    doneStatusCount++;
                    break;
                case NEW:
                    newStatusCount++;
                    break;
            }
        }
        // Определяем новый статус
        TaskStatus forChangeStatus = TaskStatus.IN_PROGRESS;
        if (newStatusCount == subtaskIds.size()) {   // даже если список был пустой сработает это первое условием
            forChangeStatus = TaskStatus.NEW;
        } else if (doneStatusCount == subtaskIds.size()) {
            forChangeStatus = TaskStatus.DONE;
        }
        // Записываем новый статус в эпик
        if (forChangeStatus != epic.getStatus()) {
            epic.setStatus(forChangeStatus);
        }
    }

    /**
     * +++2.1 Получение списка всех задач
    */

    // +++Получение для Task
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    // +++Получение для Subtask
    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // +++Получение для Epic
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * +++2.2 Удаление всех задач.
    */

    // +++Удаление для Task
    @Override
    public void removeTasks() {
        tasks.clear();
    }

    // +++Удаление для Subtask
    @Override
    public void removeSubtasks() {
        subtasks.clear();
        // Необходимо поменять в эпиках статус после удаления всех подзадач и очистить список подзадач
        for (Epic epic : epics.values()) {
            if (epic != null) {
                epic.setStatus(TaskStatus.NEW);
                epic.clearSubtaskIds();
            }
        }
    }

    // +++Удаление для Epic
    @Override
    public void removeEpics() {
        epics.clear();
        // Необходимо удалить все подзадачи, т.к. эпиков больше нет
        subtasks.clear();
    }

    /**
    * +++2.3 Получение по идентификатору.
    */

    // +++Получение для Task
    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id))  return null;
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    // +++Получение для Subtask
    @Override
    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id))  return null;
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    // +++Получение для Epic
    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id))  return null;
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    /**
    * +++2.4 Создание. Сам объект должен передаваться в качестве параметра.
    */

    // +++Создание для Task
    @Override
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("Задача НЕ создана, объект не инициализирован");
            return;
        }
        task.setId(++id);
        tasks.put(task.getId(), task);
    }

    // +++Создание для Subtask
    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Подзадача НЕ создана, объект не инициализирован");
            return;
        }
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            System.out.println("Подзадача НЕ создана, эпик с id = " + epicId + "  не найден");
            return;
        }
        subtask.setId(++id);
        subtasks.put(subtask.getId(), subtask);
        // Записываем в список эпика id подзадачи
        Epic epic = epics.get(epicId);
        epic.addSubtaskById(subtask.getId());
        // Синхронизируем статус в эпике
        synchronizeEpicStatus(epicId);
    }

    // +++Создание для Epic
    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Эпик НЕ создан, объект не инициализирован");
            return;
        }
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }

    /**
    * +++2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    */

    // +++Обновление для Task
    @Override
    public void updateTask(Task task) {
        if (task == null) {
            System.out.println("Задача Task НЕ обновлена, объект не инициализирован");
            return;
        }
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            System.out.println("Задача Task НЕ обновлена, id не найден");
            return;
        }
        Task originTask = tasks.get(id);
        originTask.setName(task.getName());
        originTask.setDescription(task.getDescription());
        originTask.setStatus(task.getStatus());
    }

    // +++Обновление для Subtask
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Задача Subtask НЕ обновлена, объект не инициализирован");
            return;
        }
        int id = subtask.getId();
        if (!subtasks.containsKey(id)) {
            System.out.println("Задача Subtask НЕ обновлена, id не найден");
            return;
        }
        Subtask originSubtask = subtasks.get(id);
        originSubtask.setName(subtask.getName());
        originSubtask.setDescription(subtask.getDescription());
        originSubtask.setStatus(subtask.getStatus());
        // синхронизируем статус в эпике
        int epicId = originSubtask.getEpicId();
        synchronizeEpicStatus(epicId);
    }

    // +++Обновление для Epic
    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Задача Epic НЕ обновлена, объект не инициализирован");
            return;
        }
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            System.out.println("Задача Epic НЕ обновлена, id не найден");
            return;
        }
        Epic originEpic = epics.get(id);
        originEpic.setName(epic.getName());
        originEpic.setDescription(epic.getDescription());
        // Статус не меняем, он рассчитывается по статусам подзадач
    }

    /**
    * +++2.6 Удаление по идентификатору.
    */

    // +++Удаление для Task
    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        tasks.remove(id);
    }

    // +++Удаление для Subtask
    @Override
    public void removeSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        // Удаляем подзадачу
        subtasks.remove(id);
        // Смотрим Эпик к которому он принадлежал
        if (!epics.containsKey(epicId)) {
            return;
        }
        Epic epic = epics.get(epicId);
        epic.removeSubtaskById(id); // У Эпика удаляем подзадачу
        synchronizeEpicStatus(epicId); // У Эпика синхронизируем статус по подзадачам
    }

    // +++Удаление для Epic
    @Override
    public void removeEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId); // вместе с эпиком удаляем все его подзадачи
        }
        epics.remove(id);
    }

    /**
    * +++3.1 Получение списка всех подзадач определённого эпика.
    */

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик с таким id не найден");
            return null;
        }
        // Получаем список подзадач эпика
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            if (!subtasks.containsKey(subtaskId)) {
                continue;
            }
            // Собираем в ArrayList для выдачи
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    /**
     * Возвращать последние 10 просмотренных задач
     */

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
