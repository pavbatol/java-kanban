import managers.FileBackedTaskManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path path = Paths.get("resources", "back.csv");
        FileBackedTaskManager.main(new String[] {path.toString()});
    }
}
