package managers;

import exceptions.ValidateException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import util.Managers;
import validators.CrossingTimeValidator;
import validators.DurationTimeValidator;
import validators.Validator;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int itemId;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;
    private final HistoryManager historyManager;
    private boolean neededPrioritySort;
    private final TreeSet<Task> prioritizedTasks;
    private final TimeManager timeManager;

    public InMemoryTaskManager() {
        itemId = -1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        timeManager = new TimeManager(15);
        neededPrioritySort = true;  // Flag for sorting tasks, /true for first, for the cause of after loadFromFile()/
        prioritizedTasks = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime() == null) {
                return 1;
            } else if (task2.getStartTime() == null) {
                return -1;
            } else {
                return task1.getStartTime().isAfter(task2.getStartTime()) ? 1
                        : task1.getStartTime().isBefore(task2.getStartTime()) ? -1 : 0;
            }
        });
    }

    @Override
    public int addTask(Task task) {
        if (task == null) {
            System.out.println("Задача НЕ создана, объект не инициализирован");
            return -1;
        }
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        neededPrioritySort = true;
        return task.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Подзадача НЕ создана, объект не инициализирован");
            return -1;
        }
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            System.out.println("Подзадача НЕ создана, эпик с id = " + epicId + "  не найден");
            return -1;
        }
        subtask.setId(getNewId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.addSubtaskById(subtask.getId()); // Записываем в список эпика id подзадачи
        synchronizeEpicWithSubtasks(epicId); // Синхронизируем статус и врем в эпике
        neededPrioritySort = true;
        return subtask.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Эпик НЕ создан, объект не инициализирован");
            return -1;
        }
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
        neededPrioritySort = true;
        return epic.getId();
    }

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

        for (Validator validator : getTaskValidators()) {
            try {
                validator.validate(task);
            } catch (ValidateException e) {
                System.out.println(e.getMessage() + " Задача Task НЕ обновлена");
                return;
            }
        }

        Task originTask = tasks.get(id);
        if (originTask != null) {
            originTask.setName(task.getName());
            originTask.setDescription(task.getDescription());
            originTask.setStatus(task.getStatus());
            originTask.setDuration(task.getDuration());
            originTask.setStartTime(task.getStartTime());
            neededPrioritySort = true;
            timeManager.occupyFor(originTask, false); // пометим время
        } else {
            System.out.println("Задача Task НЕ обновлена, по id " + id + " лежит null");
        }
    }

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

        for (Validator validator : getTaskValidators()) {
            try {
                validator.validate(subtask);
            } catch (ValidateException e) {
                System.out.println(e.getMessage() + " Задача Subtask НЕ обновлена");
                return;
            }
        }

        Subtask originSubtask = subtasks.get(id);
        if (originSubtask != null) {
            originSubtask.setName(subtask.getName());
            originSubtask.setDescription(subtask.getDescription());
            originSubtask.setStatus(subtask.getStatus());
            originSubtask.setDuration(subtask.getDuration());
            originSubtask.setStartTime(subtask.getStartTime());
            synchronizeEpicWithSubtasks(subtask.getEpicId()); // синхронизируем статус и время в эпике
            neededPrioritySort = true;
            timeManager.occupyFor(originSubtask, false); // время
        } else {
            System.out.println("Задача Subtask НЕ обновлена, по id " + id + " лежит null");
        }
    }

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
        if (originEpic != null) {
            originEpic.setName(epic.getName());
            originEpic.setDescription(epic.getDescription());
            // ... Статус, duration, startTime, endTime не меняем, они рассчитывается по подзадачам
            neededPrioritySort = true;
        } else {
            System.out.println("Задача Epic НЕ обновлена, по id " + id + " лежит null");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        Task task =  tasks.remove(id);
        historyManager.remove(id);
        neededPrioritySort = true;
        if (task != null) {
            timeManager.freeFor(task); // освободим время
        }

    }

    @Override
    public void removeSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        Subtask subtask = subtasks.remove(id);
        historyManager.remove(id);
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            epic.removeSubtaskById(id); // У Эпика удаляем подзадачу
            synchronizeEpicWithSubtasks(epicId); // У Эпика синхронизируем статус и время по подзадачам
        }
        neededPrioritySort = true;
        if (subtask != null) {
            timeManager.freeFor(subtask); // освободим время
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        // вместе с эпиком удаляем все его подзадачи, и эти подзадачи из истории и освободим время
        epics.get(id).getSubtaskIds().forEach(subtaskId -> {
            Subtask subtask = subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            if (subtask != null) {
                timeManager.freeFor(subtask); // освободим время
            }
        });
        // удаляем сам эпик и его из истории
        epics.remove(id);
        historyManager.remove(id);
        neededPrioritySort = true;
    }

    @Override
    public void removeTasks() {
        tasks.forEach((id, task) -> {
            historyManager.remove(id); // удаляем из истории
            if (task != null) {
                timeManager.freeFor(task); // освободим время
            }
        });
        tasks.clear();
        neededPrioritySort = true;
    }

    @Override
    public void removeSubtasks() {
        subtasks.forEach((id, subtask) -> {
            historyManager.remove(id);  // удаляем из истории
            if (subtask != null) {
                timeManager.freeFor(subtask); // освободим время
            }
        });
        subtasks.clear();
        // Необходимо поменять в эпиках статус после удаления всех подзадач и очистить список подзадач
        for (Epic epic : epics.values()) {
            if (epic != null) {
                epic.setStatus(TaskStatus.NEW);
                epic.clearSubtaskIds();
            }
        }
        neededPrioritySort = true;
    }

    @Override
    public void removeEpics() {
        epics.forEach((id, epic) -> historyManager.remove(id));
        epics.clear();
        // Необходимо удалить все подзадачи, и их же из истории т.к. эпиков больше нет, и освободим время
        subtasks.forEach((id, subtask) -> {
            historyManager.remove(id); //из истории удалим
            if (subtask != null) {
                timeManager.freeFor(subtask); // освободим время
            }
        });
        subtasks.clear();
        neededPrioritySort = true;
    }

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

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик с таким id не найден");
            return null;
        }
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (int subtaskId : epics.get(epicId).getSubtaskIds()) {
            if (!subtasks.containsKey(subtaskId)) continue;
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    private void synchronizeEpicStatus(int epicId) {
        if (!epics.containsKey(epicId) || epics.get(epicId) == null) {
            System.out.println("Объект не найден");
            return;
        }
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        int doneStatusCount = 0; // считать статус DON
        int newStatusCount = 0; // считать статус NEW
        boolean isBreak = false;
        for (int subtaskId : subtaskIds) {
            if (isBreak) break;
            if (!subtasks.containsKey(subtaskId) || subtasks.get(subtaskId) == null) continue;
            TaskStatus subtaskStatus = subtasks.get(subtaskId).getStatus();
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

    private void synchronizeEpicTimes(int epicId){
        if (!epics.containsKey(epicId) || epics.get(epicId) == null) {
            System.out.println("Объект не найден");
            return;
        }
        Epic epic = epics.get(epicId);
        epic.setStartTime(null);
        epic.setEndTime(null);
        epic.setDuration(0);
        for (int id : epic.getSubtaskIds()) {
            if (!subtasks.containsKey(id) || subtasks.get(id) == null) {
                continue;
            }
            Subtask subtask = subtasks.get(id);
            epic.setDuration(epic.getDuration() + subtask.getDuration()); // суммируем длительность
            if (subtask.getStartTime() != null) {
                if (epic.getStartTime() == null || epic.getStartTime().isAfter(subtask.getStartTime())) {
                    epic.setStartTime(subtask.getStartTime());
                }
            }
            if (subtask.getEndTime() != null) {
                if (epic.getEndTime() == null || epic.getEndTime().isBefore(subtask.getEndTime())) {
                    epic.setEndTime(subtask.getEndTime());
                }
            }
        }
    }

    private void synchronizeEpicWithSubtasks(int epicId) {
        synchronizeEpicStatus(epicId);
        synchronizeEpicTimes(epicId);
    }

    private int getNewId() {
        return ++itemId;
    }

    protected Map<Integer, Task> getTasksKeeper() {
        return tasks;
    }

    protected Map<Integer, Subtask> getSubtasksKeeper() {
        return subtasks;
    }

    protected Map<Integer, Epic> getEpicsKeeper() {
        return epics;
    }

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }

    protected TimeManager getTimesManager() {
        return timeManager;
    }

    public List<Task> getPrioritizedTasks() {
        if (neededPrioritySort) {
            fillPrioritizedTasks();
            neededPrioritySort = false;
        }
        return new ArrayList<>(prioritizedTasks);
    }

    private void fillPrioritizedTasks() {
        prioritizedTasks.clear();
        tasks.forEach((id, task) -> {if (task != null) prioritizedTasks.add(task);});
        subtasks.forEach((id, task) -> {if (task != null) prioritizedTasks.add(task);});
    }

    private List<Validator> getTaskValidators(){
        return List.of(
                new DurationTimeValidator(),
                new CrossingTimeValidator(timeManager)
        );
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
