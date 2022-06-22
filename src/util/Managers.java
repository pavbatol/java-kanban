package util;

import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import managers.TaskManager;
import managers.InMemoryTaskManager;

public final class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
