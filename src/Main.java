import managers.FileBackedTasksManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path path = Paths.get("resources", "back.csv");
        FileBackedTasksManager.main(path);
    }
}
