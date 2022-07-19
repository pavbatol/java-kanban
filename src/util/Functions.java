package util;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import static tasks.TaskType.*;

public final class Functions {
    private Functions() {
    }

    public static boolean isPositiveInt(String str) {
        try {
            return Integer.parseInt(str) >= 0;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static TaskType getTaskType(Task task) {
        TaskType type = TASK;
        if (task.getClass() == Subtask.class) {
            type = SUBTASK;
        } else if (task.getClass() == Epic.class) {
            type = EPIC;
        }
        return type;
    }

}
