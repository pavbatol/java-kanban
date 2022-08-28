package managers;

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
import java.util.Objects;
import java.util.stream.Collectors;

import static tasks.TaskStatus.NEW;

public class HTTPTaskManager extends FileBackedTaskManager{
    public static final String KEY = "taskManager";
    private final transient String url;
    private transient KVTaskClient client;

    public HTTPTaskManager(String url) {
        super(Path.of(""));
        this.url = url;
        try {
            this.client = new KVTaskClient(this.url);
            System.out.println("Клиент запущен. Ключ сохранения/восстановления: " + KEY + ", адрес: " + this.url);
        } catch (RuntimeException e) {
            System.out.println("!Не удалось запустить HTTP-Client\n" + e.getMessage());
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

        task1.setDuration(timeStep * 2L);
        task2.setDuration(timeStep * 2L);
        subtask1.setDuration(timeStep * 2L);
        subtask2.setDuration(timeStep * 2L);
        subtask3.setDuration(timeStep * 2L);

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

        HTTPTaskManager tm2 = loadFromServer(tm.client, KEY);
        System.out.println("\nМенеджер оригинальный");
        System.out.println(tm);
        System.out.println("\nМенеджер загруженный с сервера");
        System.out.println(tm2);

        //server.stop();
    }

    public static HTTPTaskManager loadFromServer(KVTaskClient client, String key) {
        HTTPTaskManager htm = Managers.getNewHTTPTaskManager();
        if (client == null) {
            System.out.println("Клиент не запущен (null), загрузка отменена");
            return null;
        }
        String json = client.load(key);
        JsonElement jsonElement = JsonParser.parseString(json);
        if(!jsonElement.isJsonObject()) {
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return null;
        }

        Gson gson = Managers.getGson();
        JsonObject root = jsonElement.getAsJsonObject();

        //Счетчик id
        htm.itemId = root.get("lastId").getAsInt();
        //Задачи
        JsonArray jTasks = root.get("tasks").getAsJsonArray();
        jTasks.forEach(jTask -> {
            Task task =  gson.fromJson(jTask, Task.class);
            htm.getTasksKeeper().put(task.getId(), task);
            htm.getTimeManager().occupyFor(task, false);
        });
        //Эпики
        JsonArray jEpics = root.get("epics").getAsJsonArray();
        jEpics.forEach(jTask -> {
            Epic task =  gson.fromJson(jTask, Epic.class);
            htm.getEpicsKeeper().put(task.getId(), task);
        });
        //Подзадачи
        JsonArray jSubtasks = root.get("subtasks").getAsJsonArray();
        jSubtasks.forEach(jTask-> {
            Subtask task =  gson.fromJson(jTask, Subtask.class);
            htm.getSubtasksKeeper().put(task.getId(), task);
            htm.getTimeManager().occupyFor(task, false);
        });
        //Приоритетные по времени
        htm.fillPrioritizedTasks();
        //История
        JsonArray jHistory = root.get("history").getAsJsonArray();
        boolean isRev = false;
        int size = jHistory.size();
        if (!((InMemoryHistoryManager) htm.getHistoryManager()).isNormalOrder()) {
            isRev = true;
        }
        for (int i = isRev ? size - 1 : 0; isRev ? i >=0 : i < size ; i += isRev ? -1 : 1) {
            int id =  jHistory.get(i).getAsInt();
            if (htm.getTasksKeeper().containsKey(id)) {
                htm.getHistoryManager().add(htm.getTasksKeeper().get(id));
            } else if (htm.getSubtasksKeeper().containsKey(id)) {
                htm.getHistoryManager().add(htm.getSubtasksKeeper().get(id));
            } else if (htm.getEpicsKeeper().containsKey(id)) {
                htm.getHistoryManager().add(htm.getEpicsKeeper().get(id));
            }
        }
        return htm;
    }

    @Override
    protected void save() throws ManagerSaveException {
        if (client == null) {
            throw new ManagerSaveException("Клиент не запущен, сохранение не выполнено");
        }
        Gson gson = Managers.getGson();
        String jsonBuilder;
        jsonBuilder = '{'
                + "\"lastId\": "
                + itemId
                + ",\"tasks\": "
                + gson.toJson(getTasks())
                + ",\"epics\": "
                + gson.toJson(getEpics())
                + ",\"subtasks\": "
                + gson.toJson(getSubtasks())
                + ",\"history\": "
                + gson.toJson(getHistory().stream()
                        .filter(Objects::nonNull).map(Task::getId).collect(Collectors.toList()))
                + '}';
        client.put(KEY, jsonBuilder);
    }

    public KVTaskClient getClient() {
        return client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HTTPTaskManager that = (HTTPTaskManager) o;
        return url.equals(that.url)
                && Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                url,
                client
        );
    }
}
