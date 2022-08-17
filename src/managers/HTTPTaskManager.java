package managers;

import api.GsonAdapters.HistoryManagerAdapter;
import api.GsonAdapters.LocalDateTimeAdapter;
import api.GsonAdapters.TimeManagerAdapter;
import api.KVServer;
import api.KVTaskClient;
import com.google.gson.*;
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
    private final transient String key;
    private final transient String url;
    private final transient Gson gson;
    private transient KVTaskClient client;

    public HTTPTaskManager(String url) {
        super(Path.of(""));
        this.key =  generateKey();
        this.url = url;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
                .registerTypeAdapter(TimeManager.class, new TimeManagerAdapter())
                .create();
        try {
            this.client = new KVTaskClient(this.url);
            //System.out.println("Клиент запущен. Ключ сохранения/восстановления: " + key + ", хост: " + this.host);
        } catch (RuntimeException e) {
            System.out.println("Не удалось запустить HTTP-Client\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        KVServer server;
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        HTTPTaskManager tm = args.length == 0 ? Managers.getNewHTTPTaskManager() : new HTTPTaskManager(args[0]);

        Epic epic1 = new Epic("name0", "description0");
        Epic epic2 = new Epic("name0", "description0");
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        Task task1 = new Task("name1", "description1", NEW);
        Task task2 = new Task("name2", "description2", NEW);
        Subtask subtask1 = new Subtask("name4", "description4", NEW, epic1.getId());
        Subtask subtask2 = new Subtask("name5", "description5", NEW, epic1.getId());
        Subtask subtask3 = new Subtask("name5", "description5", NEW, epic2.getId());

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
        subtask3.setStartTime(subtask1.getEndTime());

        tm.addTask(task1);
        tm.addTask(task2);
        tm.addSubtask(subtask1);
        tm.addSubtask(subtask2);
        tm.addSubtask(subtask3);

        tm.getTaskById(task1.getId());
        tm.getTaskById(task2.getId());
        tm.getSubtaskById(subtask1.getId());
        tm.getSubtaskById(subtask2.getId());
        tm.getSubtaskById(subtask3.getId());


        HTTPTaskManager tm2 = loadFromServer(tm.client, tm.key);
        System.out.println("\nМенеджер оригинальный");
        System.out.println(tm);
        System.out.println("\nМенеджер загруженный с сервера");
        System.out.println(tm2);

    }

    public static HTTPTaskManager loadFromServer(KVTaskClient client, String key) {
        HTTPTaskManager hm = Managers.getNewHTTPTaskManager();
        String json = client.load(key);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        JsonElement jsonElement = JsonParser.parseString(json);
        if(!jsonElement.isJsonObject()) {
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return null;
        }

        JsonObject root = jsonElement.getAsJsonObject();
        hm.itemId = root.get("itemId").getAsInt();
        //Задачи
        JsonObject joTasks = root.get("tasks").getAsJsonObject();
        joTasks.entrySet().forEach(e -> {
            JsonObject joTask = joTasks.get(e.getKey()).getAsJsonObject();
            Task task =  gson.fromJson(joTask, Task.class);
            hm.getTasksKeeper().put(task.getId(), task);
            hm.getTimeManager().occupyFor(task, false);
        });
        //Подзадачи
        JsonObject joSubtasks = root.get("subtasks").getAsJsonObject();
        joSubtasks.entrySet().forEach(e -> {
            JsonObject joTask = joSubtasks.get(e.getKey()).getAsJsonObject();
            Subtask task =  gson.fromJson(joTask, Subtask.class);
            hm.getSubtasksKeeper().put(task.getId(), task);
            hm.getTimeManager().occupyFor(task, false);
        });
        //Эпики
        JsonObject joEpics = root.get("epics").getAsJsonObject();
        joEpics.entrySet().forEach(e -> {
            JsonObject joTask = joEpics.get(e.getKey()).getAsJsonObject();
            Epic task =  gson.fromJson(joTask, Epic.class);
            hm.getEpicsKeeper().put(task.getId(), task);
        });
        //История
        JsonObject johistoryManager = root.get("historyManager").getAsJsonObject();
        JsonArray johistory = johistoryManager.get("history").getAsJsonArray();
        for (JsonElement el : johistory) {
            int id = el.getAsInt();
            if (hm.getTasksKeeper().containsKey(id)) {
                hm.getHistoryManager().add(hm.getTasksKeeper().get(id));
            } else if (hm.getSubtasksKeeper().containsKey(id)) {
                hm.getHistoryManager().add(hm.getSubtasksKeeper().get(id));
            } else if (hm.getEpicsKeeper().containsKey(id)) {
                hm.getHistoryManager().add(hm.getEpicsKeeper().get(id));
            }
        }
        return hm;
    }

    @Override
    protected void save() throws ManagerSaveException {
        if (client == null) {
            throw new ManagerSaveException("Клиент не запущен, сохранение не выполнено");
        }
        String json;
        try {
            json = gson.toJson(this);
        } catch (Exception e) {
            throw new ManagerSaveException("Не удалось перевести в JSON, сохранение не выполнено\n" + e.getMessage());
        }
        client.put(key, json);
    }

    private String generateKey() {
        return "taskManager";
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public Gson getGson() {
        return gson;
    }

    public KVTaskClient getClient() {
        return client;
    }
}
