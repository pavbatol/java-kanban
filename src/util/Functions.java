package util;

import managers.InMemoryTaskManager;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.util.List;

import static tasks.TaskType.*;

public final class Functions {
    private Functions() {
    }

    /**
     * Узнать является ли строка str числом типа int и что оно не отрицательное
     * @param str Строка для проверки
     * @return Является ли не отрицательным int-ом
     */
    public static boolean isPositiveInt(String str) {
        try {
            return Integer.parseInt(str) >= 0;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static TaskType getTaskType(Class<?> cl) {
        if (cl == null) return null;
        TaskType type = null;
        if (cl == Task.class) {
            type = TASK;
        } else  if (cl == Subtask.class) {
            type = SUBTASK;
        } else if (cl == Epic.class) {
            type = EPIC;
        }
        return type;
    }

    /**
     * Получить тип задачи
     * @param task Задача
     * @return TaskType  или null
     */
    // TODO: 02.08.2022 Удалить
//    public static TaskType getTaskType(Task task) {
//        if (task == null) return null;
//        Class<?> cl = task.getClass();
//        return getTaskType(cl);
//    }

    /**
     * Получить тип задачи
     * @param taskId id задачи
     * @param tm Task-менеджер
     * @return TaskType  или null
     */
    // TODO: 02.08.2022 Удалить
//    public static TaskType getTaskType(int taskId, TaskManager tm) {
//        if (tm == null) return null;
//        Task task = getAnyTypeTaskById(taskId, tm);
//        return getTaskType(task);
//    }

    /**
     * Получить список задач типа "taskType"
     * @param tm Task-менеджер типа интерфейса TaskManager
     * @param taskType Тип ожидаемых задач
     * @return Вернет Список задач типа "taskType" или null
     */
    public static List<? extends Task> getAnyTypeTasksForType(TaskManager tm, TaskType taskType) {
        if (tm == null || taskType == null) {
            return null;
        }
        List<? extends Task> result = null;
        switch (taskType) {
            case TASK: result = tm.getTasks();
                break;
            case SUBTASK: result = tm.getSubtasks();
                break;
            case EPIC: result = tm.getEpics();
                break;
        }
        return result;
    }

    /**
     * Получить задачу по id в нЕзависимости от ее типа
     * @param taskId id задачи
     * @param tm Task-менеджер типа интерфейса TaskManager
     * @return Вернет Task или null
     */
    public static Task getAnyTypeTaskById(int taskId, TaskManager tm) {
        if (tm == null) return null;
        Task task = tm.getTaskById(taskId);
        if (task == null) {
            task = tm.getSubtaskById(taskId);
        }
        if (task == null) {
            task = tm.getEpicById(taskId);
        }
        return task;
    }

    /**
     * Получить задачу по id в зАвисимости от ее типа
     * @param taskId id задачи
     * @param tm Task-менеджер типа интерфейса TaskManager
     * @param taskType Какого типа ищем задачу
     * @return Вернет задачу типа "taskType" или null
     */
    public static Task getAnyTypeTaskByIdForType(int taskId, TaskManager tm, TaskType taskType) {
        if (tm == null || taskType == null) {
            return null;
        }
        Task task = null;
        switch (taskType) {
            case TASK: task = tm.getTaskById(taskId);
                break;
            case SUBTASK: task = tm.getSubtaskById(taskId);
                break;
            case EPIC: task = tm.getEpicById(taskId);
                break;
        }
        return task;
    }

    /**
     * Добавить задачу любого типа
     * @param tm менеджер InMemoryTaskManager
     * @param task Задача любого типа: Task, SubTask, Epic
     * @return Вернет установленный id для задачи. Если не добавил, то вернет -1
     * @throws IllegalArgumentException
     */
    public static int addAnyTypeTask (InMemoryTaskManager tm, Task task) throws IllegalArgumentException {
        switch (task.getType()) {
            case TASK: return tm.addTask(task);
            case SUBTASK: return tm.addSubtask((Subtask) task);
            case EPIC: return tm.addEpic((Epic) task);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    /**
     * Обновитьзадачу любого типа
     * @param tm менеджер InMemoryTaskManager
     * @param task Задача любого типа: Task, SubTask, Epic
     * @throws IllegalArgumentException
     */
    public static void updateAnyTypeTask(InMemoryTaskManager tm, Task task) throws IllegalArgumentException {
        switch (task.getType()) {
            case TASK: tm.updateTask(task);
                break;
            case SUBTASK: tm.updateSubtask((Subtask) task);
                break;
            case EPIC: tm.updateEpic((Epic) task);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    /**
     * Удалить люобой тип задачи по id
     * @param tm менеджер InMemoryTaskManager
     * @param taskId id задачи любого типа: Task, SubTask, Epic
     * @throws IllegalArgumentException
     */
    public static void removedAnyTypeTaskByI(InMemoryTaskManager tm, int taskId) throws IllegalArgumentException {
        TaskType taskType = tm.getTypeByTaskId(taskId);
        if (taskType == null) {
            return;
        }
        switch (taskType) {
            case TASK: tm.removeTaskById(taskId);
                break;
            case SUBTASK: tm.removeSubtaskById(taskId);
                break;
            case EPIC: tm.removeEpicById(taskId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

}
