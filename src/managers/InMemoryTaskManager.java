package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import util.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int itemId;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        itemId = -1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    /**
     * Добавить задачу
     */
    @Override
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("Задача НЕ создана, объект не инициализирован");
            return;
        }
        task.setId(getNewId());
        tasks.put(task.getId(), task);
    }

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
        subtask.setId(getNewId());
        subtasks.put(subtask.getId(), subtask);
        // Записываем в список эпика id подзадачи
        Epic epic = epics.get(epicId);
        epic.addSubtaskById(subtask.getId());
        // Синхронизируем статус в эпике
        synchronizeEpicStatus(epicId);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Эпик НЕ создан, объект не инициализирован");
            return;
        }
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
    }

    /**
     * Обновление
     */
    @Override
    public void updateTask(Task task) {
        if (task == null) {
            System.out.println("Задача Tasks.Task НЕ обновлена, объект не инициализирован");
            return;
        }
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            System.out.println("Задача Tasks.Task НЕ обновлена, id не найден");
            return;
        }
        Task originTask = tasks.get(id);
        originTask.setName(task.getName());
        originTask.setDescription(task.getDescription());
        originTask.setStatus(task.getStatus());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Задача Tasks.Subtask НЕ обновлена, объект не инициализирован");
            return;
        }
        int id = subtask.getId();
        if (!subtasks.containsKey(id)) {
            System.out.println("Задача Tasks.Subtask НЕ обновлена, id не найден");
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

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Задача Tasks.Epic НЕ обновлена, объект не инициализирован");
            return;
        }
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            System.out.println("Задача Tasks.Epic НЕ обновлена, id не найден");
            return;
        }
        Epic originEpic = epics.get(id);
        originEpic.setName(epic.getName());
        originEpic.setDescription(epic.getDescription());
        // Статус не меняем, он рассчитывается по статусам подзадач
    }

    /**
     * Удаление по идентификатору.
     */
    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        historyManager.remove(id);
        // Смотрим Эпик к которому он принадлежал
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            epic.removeSubtaskById(id); // У Эпика удаляем подзадачу
            synchronizeEpicStatus(epicId); // У Эпика синхронизируем статус по подзадачам
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        // вместе с эпиком удаляем все его подзадачи, и эти подзадачи из истории
        epics.get(id).getSubtaskIds().forEach(subtaskId -> {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        });
        // удаляем сам эпик и его из истории
        epics.remove(id);
        historyManager.remove(id);
    }

    /**
     * Удаление всех задач.
     */
    @Override
    public void removeTasks() {
        tasks.forEach((id, task) -> historyManager.remove(id)); // удаляем из истории
        tasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.forEach((id, subtask) -> historyManager.remove(id)); // удаляем из истории
        subtasks.clear();
        // Необходимо поменять в эпиках статус после удаления всех подзадач и очистить список подзадач
        for (Epic epic : epics.values()) {
            if (epic != null) {
                epic.setStatus(TaskStatus.NEW);
                epic.clearSubtaskIds();
            }
        }
    }

    @Override
    public void removeEpics() {
        epics.forEach((id, epic) -> historyManager.remove(id));
        epics.clear();
        // Необходимо удалить все подзадачи, и их же из истории т.к. эпиков больше нет
        subtasks.forEach((id, subtask) -> historyManager.remove(id));
        subtasks.clear();
    }

    /**
     * Получение по идентификатору.
     */
    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) return null;
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id)) return null;
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) return null;
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    /**
     * Получение списка всех задач
    */
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
    * Получение списка всех подзадач определённого эпика.
    */
    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик с таким id не найден");
            return null;
        }
        List<Subtask> epicSubtasks = new ArrayList<>();
        // Получаем список подзадач эпика и собираем в ArrayList для выдачи
        for (int subtaskId : epics.get(epicId).getSubtaskIds()) {
            if (!subtasks.containsKey(subtaskId)) continue;
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    /**
     * Возвращать последние просмотренные задачи
     */
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
        List<Integer> subtaskIds = epic.getSubtaskIds();
        int doneStatusCount = 0; // считать статус DON
        int newStatusCount = 0; // считать статус NEW
        boolean isBreak = false;
        for (int subtaskId : subtaskIds) {
            if (isBreak) break;
            if (!subtasks.containsKey(subtaskId) || subtasks.get(subtaskId) == null) continue;
            TaskStatus subtaskStatus = subtasks.get(subtaskId).getStatus(); // Получаем статус каждой подзадачи
            if (subtaskStatus == null) continue;
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

    //Генератор id
    private int getNewId() {
        return ++itemId;
    }

    public HistoryManager getHistoryManager() { // TODO: 19.07.2022 Нужен ли
        return historyManager;
    }



    @Override
    public String toString() {
        final Map<String, String> strs = new HashMap<>(Map.of(
                "tasks", "",
                "epics", "",
                "subtasks", ""));

        // Для каждого ключа составляем соответствующую строку
        tasks.forEach((id, task) -> strs.put("tasks",  strs.get("tasks") + "\t\t" + task.toString() + "\n"));
        epics.forEach((id, task) -> strs.put("epics", strs.get("epics") + "\t\t" + task.toString() + "\n"));
        subtasks.forEach((id, task) -> strs.put("subtasks", strs.get("subtasks") + "\t\t" + task.toString() + "\n"));

        return "InMemoryTaskManager{" +
                "\n\titemId=" + itemId + " (последний отданный id)" + //"," +
                "\n\ttasks=\n" + strs.get("tasks") +
                "\tepics=\n" + strs.get("epics") +
                "\tsubtasks=\n" + strs.get("subtasks") +
                "\thistoryManager=" + historyManager.toString().replace("\n", "\n\t") + "\n" +
                '}';
    }
}
