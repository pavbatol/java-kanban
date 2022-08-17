package managers;

import api.GsonAdapters.InMemoryHistoryManagerAdapter;
import api.GsonAdapters.LocalDateTimeAdapter;
import api.GsonAdapters.PathAdapter;
import api.GsonAdapters.TimeManagerAdapter;
import api.KVServer;
import api.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Managers;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static tasks.TaskStatus.NEW;

public class HTTPTaskManager extends FileBackedTaskManager{
    private transient final String key;
    private final String host;
    private transient KVTaskClient client;

    public HTTPTaskManager(String host) {
        super(Path.of(""));
        this.key = generateKey();
        this.host = host;
        this.client = null;
        try {
            this.client = new KVTaskClient("http://localhost:8078");
            System.out.println("Клиент запущен. Ключ сохранения/восстановления: " + key + ", хост: " + this.host);
        } catch (RuntimeException e) {
            System.out.println("Не удалось запустить HTTP-Client\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        KVServer server = null;
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        HTTPTaskManager tm = new HTTPTaskManager(args[0]); //Managers.getNewFileBackedTaskManager();

        Epic epic1 = new Epic("name0", "description0");
        Epic epic2 = new Epic("name0", "description0");
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        Task task1 = new Task("name1", "description1", NEW);
        Task task2 = new Task("name2", "description2", NEW);
        Subtask subtask1 = new Subtask("name4", "description4", NEW, epic1.getId());
        Subtask subtask2 = new Subtask("name5", "description5", NEW, epic1.getId());

        final int timeStep = tm.getTimeStepByTimeManager();
        LocalDateTime start = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0, 0);

        task1.setDuration(timeStep * 2);
        task2.setDuration(timeStep * 2);
        subtask1.setDuration(timeStep * 2);
        subtask2.setDuration(timeStep * 2);

        task1.setStartTime(start);
        task2.setStartTime(task1.getEndTime());
        subtask2.setStartTime(task2.getEndTime());
        subtask1.setStartTime(subtask2.getEndTime());

        tm.addTask(task1);
        tm.addTask(task2);
        tm.addSubtask(subtask1);
        tm.addSubtask(subtask2);

        tm.getTaskById(task1.getId());
        tm.getTaskById(task2.getId());
        tm.getSubtaskById(subtask1.getId());
        tm.getSubtaskById(subtask2.getId());

        System.out.println("\nМенеджер-оригинал");
        System.out.println(tm);




    }

    public HTTPTaskManager load() {
        HTTPTaskManager manager = new HTTPTaskManager(host);



        return manager;
    }

    @Override
    protected void save() throws ManagerSaveException {
        if (client == null) {
            throw new ManagerSaveException("Клиент не запущен, сохранение не выполнено");
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeHierarchyAdapter(Path.class, new PathAdapter())
                .registerTypeAdapter(InMemoryHistoryManager.class, new InMemoryHistoryManagerAdapter())
                .registerTypeAdapter(TimeManager.class, new TimeManagerAdapter())
                .create();

        String json;
        try {
            json = gson.toJson(this);
        } catch (Exception e) {
            throw new ManagerSaveException("Не удалось перевести в JSON, сохранение не выполнено\n" + e.getMessage());
        }
        client.put(key, json);

    }

    private String generateKey() {
        return "" + System.currentTimeMillis();
    }

    protected String getKey() {
        return key;
    }
}
