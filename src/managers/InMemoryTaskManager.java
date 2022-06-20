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
    private int id;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;
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
        List<Integer> subtaskIds = epic.getSubtaskIds();
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
     * Получение списка всех задач
    */
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Удаление всех задач.
    */
    @Override
    public void removeTasks() {
        tasks.clear();
    }

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

    @Override
    public void removeEpics() {
        epics.clear();
        // Необходимо удалить все подзадачи, т.к. эпиков больше нет
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
    * Добавить задачу
    */
    @Override
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("Задача НЕ создана, объект не инициализирован");
            return;
        }
        task.setId(++id);
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
        subtask.setId(++id);
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
        epic.setId(++id);
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
    }

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
    * Получение списка всех подзадач определённого эпика.
    */
    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик с таким id не найден");
            return null;
        }
        // Получаем список подзадач эпика
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtaskIds();
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
