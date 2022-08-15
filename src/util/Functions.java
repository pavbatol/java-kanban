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

    public static TaskType getTaskType(int taskId, InMemoryTaskManager tm) {
        if (tm == null) {
            return null;
        }
        return tm.getTaskTypeByTaskId(taskId);
    }

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
     * @param tm Task-менеджер типа интерфейса TaskManager (Будет срабатывать заполнение истории просмотра
     *           т.к. испульзуется getTaskById)
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
     */
    public static int addAnyTypeTask (InMemoryTaskManager tm, Task task) {
        switch (task.getType()) {
            case TASK: return tm.addTask(task);
            case SUBTASK: return tm.addSubtask((Subtask) task);
            case EPIC: return tm.addEpic((Epic) task);
            default: return -1;
        }
    }

    public static int addAnyTypeTaskForType(Task task, TaskManager tm, TaskType taskType) {
        if (tm == null || taskType == null) {
            return -1;
        }
        if (task.getType() != taskType) {
            return -1;
        }
        switch (taskType) {
            case TASK: return tm.addTask(task);
            case SUBTASK: return tm.addSubtask((Subtask) task);
            case EPIC: return tm.addEpic((Epic) task);
            default: return -1;
        }
    }

    /**
     * Обновитьзадачу любого типа
     * @param tm менеджер типа TaskManager
     * @param task Задача любого типа: Task, SubTask, Epic
     * @throws IllegalArgumentException
     */
    public static void updateAnyTypeTask(TaskManager tm, Task task) throws IllegalArgumentException {
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
     * Обновить задачу если она соответствует типу "taskType"
     * @param task Задача с новыми параметрами
     * @param tm менеджер типа TaskManager
     * @param taskType Какого типа должна быть задача прешедшая на вход
     */
    public static void updateAnyTypeTaskForType(Task task, TaskManager tm, TaskType taskType) {
        if (tm == null || taskType == null) {
            return;
        }
        if (task.getType() != taskType) {
            return;
        }
        switch (taskType) {
            case TASK: tm.updateTask(task);
                break;
            case SUBTASK: tm.updateSubtask((Subtask) task);
                break;
            case EPIC: tm.updateEpic((Epic) task);
                break;
        }
    }

    /**
     * Удалить люобой тип задачи по id
     * @param tm менеджер InMemoryTaskManager
     * @param taskId id задачи любого типа: Task, SubTask, Epic
     * @throws IllegalArgumentException
     */
    public static void removedAnyTypeTaskById(InMemoryTaskManager tm, int taskId) throws IllegalArgumentException {
        TaskType taskType = tm.getTaskTypeByTaskId(taskId);
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

    /**
     * Удалить задачу по id в зАвисимости от ее типа
     * @param taskId id задачи
     * @param tm Task-менеджер типа интерфейса TaskManager
     * @param taskType Какого типа удаляем задачу
     */
    public static void removedAnyTypeTaskByIdForType(int taskId, TaskManager tm, TaskType taskType) {
        if (tm == null || taskType == null) {
            return;
        }
        switch (taskType) {
            case TASK: tm.removeTaskById(taskId);
                break;
            case SUBTASK: tm.removeSubtaskById(taskId);
                break;
            case EPIC: tm.removeEpicById(taskId);
                break;
        }
    }

    public static void removedAnyTypeTasksForType(TaskManager tm, TaskType taskType) {
        if (tm == null || taskType == null) {
            return;
        }
        switch (taskType) {
            case TASK: tm.removeTasks();
                break;
            case SUBTASK: tm.removeSubtasks();
                break;
            case EPIC: tm.removeEpics();
                break;
        }
    }

}
