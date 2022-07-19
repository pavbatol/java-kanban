import managers.FileBackedTasksManager;
import util.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import managers.TaskManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path path = Paths.get("resources", "back.csv");
        FileBackedTasksManager.main(path);
    }
}
