package util;

import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

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

    /**
     * Получить тип задачи
     * @param task Задача
     * @return TaskType  или null
     */
    public static TaskType getTaskType(Task task) {
        if (task == null) return null;
        TaskType type = null;
        if ((task.getClass() == Task.class) ) {
            type = TASK;
        } else  if (task.getClass() == Subtask.class) {
            type = SUBTASK;
        } else if (task.getClass() == Epic.class) {
            type = EPIC;
        }
        return type;
    }

    /**
     * Получить тип задачи
     * @param taskId id задачи
     * @param tm Task-менеджер
     * @return TaskType  или null
     */
    public static TaskType getTaskType(int taskId, TaskManager tm) {
        if (tm == null) return null;
        Task task = getAnyTypeTaskById(taskId, tm);
        return getTaskType(task);
    }

    /**
     * Получить задачу по id в независимости от ее типа
     * @param taskId id задачи
     * @param tm Task-менеджер типа интерфейса TaskManager (Если передан FileBackedTasksManager,
     *           то будет срабатывать авто-сохранение в файл)
     * @return Task или null
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

}
