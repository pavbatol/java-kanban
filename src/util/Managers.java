package util;

import managers.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Managers {
    public static final Path path = Paths.get("resources", "back.csv");
    public static final TaskManager currentTaskManager = getDefault(); // Можно использовать как единый менеджер

    private Managers() {
    }

    public static FileBackedTaskManager getNewFileBackedTaskManager() {
        return new FileBackedTaskManager(path);
    }

    public static InMemoryTaskManager getNewInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefault() {
        return getNewInMemoryTaskManager();
    }



    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
