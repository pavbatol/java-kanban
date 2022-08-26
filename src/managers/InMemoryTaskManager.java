package managers;

import exceptions.ValidateException;
import tasks.*;
import util.Managers;
import validators.CrossingTimeValidator;
import validators.DurationTimeValidator;
import validators.Validator;

import java.util.*;

import static tasks.TaskType.*;

public class InMemoryTaskManager implements TaskManager {
    protected int itemId;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTasks;
    private final TimeManager timeManager;

    public InMemoryTaskManager() {
        itemId = -1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        timeManager = new TimeManager(15);
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
        for (Validator validator : getTaskValidators()) {
            try {
                validator.validate(task);
            } catch (ValidateException e) {
                System.out.println(e.getMessage() + " Задача Task НЕ создана");
                return -1;
            }
        }

        // TODO: 15.08.2022 Проверять на все поля как при update
        tasks.put(task.getId(), task);
        fillPrioritizedTasks();
        timeManager.occupyFor(task, false);
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
        for (Validator validator : getTaskValidators()) {
            try {
                validator.validate(subtask);
            } catch (ValidateException e) {
                System.out.println(e.getMessage() + " Задача Subtask НЕ создана");
                return -1;
            }
        }

        // TODO: 15.08.2022 Проверять на все поля как при update
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.addSubtaskById(subtask.getId());
        synchronizeEpicWithSubtasks(epicId);
        fillPrioritizedTasks();
        timeManager.occupyFor(subtask, false);
        return subtask.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Эпик НЕ создан, объект не инициализирован");
            return -1;
        }
        // TODO: 15.08.2022 Проверять на все поля как при update
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
        fillPrioritizedTasks();
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
            fillPrioritizedTasks();
            timeManager.occupyFor(originTask, false);
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
            synchronizeEpicWithSubtasks(subtask.getEpicId());
            fillPrioritizedTasks();
            timeManager.occupyFor(originSubtask, false);
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
            // TODO: 26.08.2022 Здесь вроде не надо fillPrioritizedTasks();
            fillPrioritizedTasks();
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
        fillPrioritizedTasks();
        if (task != null) {
            timeManager.freeFor(task);
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
            epic.removeSubtaskById(id);
            synchronizeEpicWithSubtasks(epicId);
        }
        fillPrioritizedTasks();
        if (subtask != null) {
            timeManager.freeFor(subtask);
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        epics.get(id).getSubtaskIds().forEach(subtaskId -> {
            Subtask subtask = subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            if (subtask != null) {
                timeManager.freeFor(subtask);
            }
        });
        // удаляем сам эпик и его из истории
        epics.remove(id);
        historyManager.remove(id);
        fillPrioritizedTasks();
    }

    @Override
    public void removeTasks() {
        tasks.forEach((id, task) -> {
            historyManager.remove(id);
            if (task != null) {
                timeManager.freeFor(task);
            }
        });
        tasks.clear();
        fillPrioritizedTasks();
    }

    @Override
    public void removeSubtasks() {
        subtasks.forEach((id, subtask) -> {
            historyManager.remove(id);
            if (subtask != null) {
                timeManager.freeFor(subtask);
            }
        });
        subtasks.clear();
        fillPrioritizedTasks();
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
        subtasks.forEach((id, subtask) -> {
            historyManager.remove(id);
            if (subtask != null) {
                timeManager.freeFor(subtask);
            }
        });
        subtasks.clear();
        fillPrioritizedTasks();
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

    protected TimeManager getTimeManager() {
        return timeManager;
    }

    public int getTimeStepByTimeManager() {
        return timeManager.getTimeStep();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected void fillPrioritizedTasks() {
        prioritizedTasks.clear();
        tasks.forEach((id, task) -> {if (task != null) prioritizedTasks.add(task);});
        subtasks.forEach((id, task) -> {if (task != null) prioritizedTasks.add(task);});
    }

    private List<Validator> getTaskValidators(){
        return List.of(
                new DurationTimeValidator(timeManager),
                new CrossingTimeValidator(timeManager)
        );
    }

    public int getLastId() {
        return this.itemId;
    }

    public TaskType getTaskTypeByTaskId(int taskId) {
        if (tasks.containsKey(taskId)) {
            return TASK;
        } else if (subtasks.containsKey(taskId)) {
            return SUBTASK;
        } else if (epics.containsKey(taskId)) {
            return SUBTASK;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        final Map<String, String> strs = new HashMap<>(Map.of(
                "tasks", "",
                "epics", "",
                "subtasks", ""));

        tasks.forEach((id, task) -> strs.put("tasks",  strs.get("tasks") + "\t\t" + task.toString() + "\n"));
        epics.forEach((id, task) -> strs.put("epics", strs.get("epics") + "\t\t" + task.toString() + "\n"));
        subtasks.forEach((id, task) -> strs.put("subtasks", strs.get("subtasks") + "\t\t" + task.toString() + "\n"));
        prioritizedTasks.forEach((task) -> strs.put("subtasks", strs.get("subtasks") + "\t\t" + task.toString() + "\n"));

        final StringBuilder prioritizedStr = new StringBuilder();
        prioritizedTasks.forEach(task -> prioritizedStr.append("\t\t").append(task.toString()).append("\n"));

        return "InMemoryTaskManager{" +
                "\n\titemId=" + itemId + " (последний отданный id)" + //"," +
                "\n\ttasks=\n" + strs.get("tasks") +
                "\tepics=\n" + strs.get("epics") +
                "\tsubtasks=\n" + strs.get("subtasks") +
                "\tprioritizedTasks=\n" + prioritizedStr +
                "\thistoryManager=" + historyManager.toString().replace("\n", "\n\t") + "\n" +
                "\ttimeManager=" + timeManager.toString().replace("\n", "\n\t") + "\n" +
                '}';
    }


}
