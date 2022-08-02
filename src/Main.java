import managers.FileBackedTaskManager;

public class Main {
    public static void main(String[] args) {
        String[] pathElements= new String[]{"resources", "back.csv"};
        FileBackedTaskManager.main(pathElements);
    }
}
