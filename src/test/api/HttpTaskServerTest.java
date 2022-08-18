package api;

import api.GsonAdapters.LocalDateTimeAdapter;
import com.google.gson.*;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.DONE;
import static tasks.TaskStatus.NEW;
import static tasks.TaskType.*;

class HttpTaskServerTest {
    static Path path = Paths.get("resourcesTest", "backTest.csv");
    public static FileBackedTaskManager tm;
    public final int port = 8080;
    public final String host = "http://localhost";
    public final String url = host +":" + port;
    static HttpTaskServer server;
    static HttpClient client;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newHttpClient();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
        }
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @BeforeEach
    public void beforeEach() {

    }

    @AfterEach
    void tearDown() {
        removeAllTasksFromManager();
    }

    @Test
    void tasks_not_GET_should_response_code_equal_405() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"test\":1}"))
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());

    }

    @Test
     void tasks_GET_should_response_body_received() throws IOException, InterruptedException {
        fillManager();
        URI uri = URI.create(url + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Task> tasks = new ArrayList<>();
        List<Subtask> subtasks = new ArrayList<>();
        List<Epic> epic = new ArrayList<>();

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jArray = jsonElement.getAsJsonArray();
        for (JsonElement joTask : jArray) {
            if (TASK.name().equals(joTask.getAsJsonObject().get("type").getAsString())) {
                tasks.add(gson.fromJson(joTask, Task.class));
            } else if (SUBTASK.name().equals(joTask.getAsJsonObject().get("type").getAsString())) {
                subtasks.add(gson.fromJson(joTask, Subtask.class));
            } else if (EPIC.name().equals(joTask.getAsJsonObject().get("type").getAsString())) {
                epic.add(gson.fromJson(joTask, Epic.class));
            }
        }

        assertEquals(tm.getTasks(), tasks, "Списки задач не равны");
        assertEquals(tm.getSubtasks(), subtasks, "Списки подзадач не равны");
        assertEquals(tm.getEpics(), epic, "Списки эпик не равны");

    }

    @Test
    void tasks_history_GET_should_response_body_received() throws IOException, InterruptedException {
        fillManager();
        URI uri = URI.create(url + "/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Task> history = new ArrayList<>();

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jArray = jsonElement.getAsJsonArray();
        for (JsonElement joTask : jArray) {
            if (TASK.name().equals(joTask.getAsJsonObject().get("type").getAsString())) {
                history.add(gson.fromJson(joTask, Task.class));
            } else if (SUBTASK.name().equals(joTask.getAsJsonObject().get("type").getAsString())) {
                history.add(gson.fromJson(joTask, Subtask.class));
            }
        }

        assertEquals(tm.getHistory(), history, "Списки истории не равны");

    }

    @Test
    void tasks_task_GET_should_response_body_received() throws IOException, InterruptedException {
        //Получить задачи Task
        fillManager();
        URI uri = URI.create(url + "/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Task> tasks = new ArrayList<>();

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jArray = jsonElement.getAsJsonArray();
        for (JsonElement joTask : jArray) {
            tasks.add(gson.fromJson(joTask, Task.class));
        }

        assertEquals(tm.getTasks(), tasks, "Списки задач не равны");

    }

    @Test
    void tasks_subtask_GET_should_response_body_received() throws IOException, InterruptedException {
        //Получить задачи Subtask
        fillManager();
        URI uri = URI.create(url + "/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Subtask> tasks = new ArrayList<>();

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jArray = jsonElement.getAsJsonArray();
        for (JsonElement joTask : jArray) {
            tasks.add(gson.fromJson(joTask, Subtask.class));
        }

        assertEquals(tm.getSubtasks(), tasks, "Списки подзадач не равны");

    }

    @Test
    void tasks_epic_GET_should_response_body_received() throws IOException, InterruptedException {
        //Получить задачи Epic
        fillManager();
        URI uri = URI.create(url + "/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Epic> tasks = new ArrayList<>();

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jArray = jsonElement.getAsJsonArray();
        for (JsonElement joTask : jArray) {
            tasks.add(gson.fromJson(joTask, Epic.class));
        }

        assertEquals(tm.getEpics(), tasks, "Списки подзадач не равны");

    }

    @Test
    void tasks_task_id_GET_should_response_body_received() throws IOException, InterruptedException {
        //Получить задачу Task по id
        removeAllTasksFromManager();
        Task task1 =  addTaskToManager();
        Task receivedTask;

        URI uri = URI.create(url + "/tasks/task?id=" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject joTask = jsonElement.getAsJsonObject();
        receivedTask = gson.fromJson(joTask, Task.class);

        assertEquals(task1, receivedTask, "Задачи не равны");

    }

    @Test
    void tasks_subtask_id_GET_should_response_body_received() throws IOException, InterruptedException {
        //Получить задачу Subtask по id
        removeAllTasksFromManager();
        Epic epic1 = addEpicToManager();
        Subtask subtask1 =  addSubtaskToManager(epic1);
        Subtask receivedTask;

        URI uri = URI.create(url + "/tasks/subtask?id=" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        System.out.println(jsonElement);

        JsonObject joTask = jsonElement.getAsJsonObject();
        receivedTask = gson.fromJson(joTask, Subtask.class);

        assertEquals(subtask1, receivedTask, "Задачи не равны");

    }

    @Test
    void tasks_epic_id_GET_should_response_body_received() throws IOException, InterruptedException {
        //Получить задачу Epic по id
        removeAllTasksFromManager();
        Epic epic1 = addEpicToManager();
        Epic receivedTask;

        URI uri = URI.create(url + "/tasks/epic?id=" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        System.out.println(jsonElement);

        JsonObject joTask = jsonElement.getAsJsonObject();
        receivedTask = gson.fromJson(joTask, Epic.class);

        assertEquals(epic1, receivedTask, "Задачи не равны");

    }

    @Test
    void tasks_task_POST_should_task_added_() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Добавить новую задачу Task
        removeAllTasksFromManager();
        Task task1 =  new Task("name_Task", "description_Task", NEW);
        Task receivedTask;

        String json = gson.toJson(task1);

        URI uri = URI.create(url + "/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код не совпадает");

        receivedTask = tm.getTaskById(0);
        task1.setId(0); // id меняются при добавлении, сделаем одинаковыми для сравнения

        assertEquals(task1, receivedTask, "Задачи не равны");
    }

    @Test
    void tasks_subtask_POST_should_task_added() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Добавить новую задачу Subtask
        removeAllTasksFromManager();
        Epic epic1 = addEpicToManager();
        Subtask subtask1 = new Subtask("name_Subtask", "description_Subtask", NEW, epic1.getId());
        Subtask receivedTask;

        String json = gson.toJson(subtask1);

        URI uri = URI.create(url + "/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код не совпадает");

        receivedTask = tm.getSubtaskById(1);
        subtask1.setId(1); // id меняются при добавлении, сделаем одинаковыми для сравнения

        assertEquals(subtask1, receivedTask, "Задачи не равны");
    }

    @Test
    void tasks_epic_POST_should_task_added() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Добавить новую задачу Epic
        removeAllTasksFromManager();
        Epic epic1 = new Epic("name_Epic", "description_Epic");
        Epic receivedTask;

        String json = gson.toJson(epic1);

        URI uri = URI.create(url + "/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код не совпадает");

        receivedTask = tm.getEpicById(0);
        epic1.setId(0); // id меняются при добавлении, сделаем одинаковыми для сравнения

        assertEquals(epic1, receivedTask, "Задачи не равны");

    }

    @Test
    void tasks_task_id_POST_should_task_updated() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Обновить задачу Task по id
        removeAllTasksFromManager();
        Task task1 =  addTaskToManager();
        Task newTask =  new Task("name_Task_2", "description_Task_2", DONE);
        newTask.setId(task1.getId());

        String json = gson.toJson(newTask);

        URI uri = URI.create(url + "/tasks/task?id=" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        assertEquals(task1, newTask, "Задачи не равны");

    }

    @Test
    void tasks_subtask_id_POST_should_task_updated() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Обновить задачу Subtask по id
        removeAllTasksFromManager();
        Epic epic1 = addEpicToManager();
        Subtask subtask1 = addSubtaskToManager(epic1);
        Subtask newTask = new Subtask("name_Subtask_2", "description_Subtask_2", DONE, epic1.getId());
        newTask.setId(subtask1.getId());

        String json = gson.toJson(newTask);

        URI uri = URI.create(url + "/tasks/subtask?id=" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        assertEquals(subtask1, newTask, "Задачи не равны");

    }

    @Test
    void tasks_epic_id_POST_should_task_updated() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Обновить задачу Epic по id
        removeAllTasksFromManager();
        Epic epic1 =  addEpicToManager();
        Epic newTask =  new Epic("name_Epic_2", "description_Epic_2");
        newTask.setId(epic1.getId());

        String json = gson.toJson(newTask);

        URI uri = URI.create(url + "/tasks/epic?id=" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        assertEquals(epic1, newTask, "Задачи не равны");
    }

    @Test
    void tasks_task_DELETE_should_all_tasks_removed() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Удалить все Task
        removeAllTasksFromManager();
        fillManager(); // наполняем

        URI uri = URI.create(url + "/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Task> tasks = tm.getTasks();

        assertEquals(0, tasks.size(), "Список не пустой");
    }

    @Test
    void tasks_subtask_DELETE_should_all_tasks_removed() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Удалить все Subtask
        removeAllTasksFromManager();
        fillManager(); // наполняем

        URI uri = URI.create(url + "/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Subtask> subtasks = tm.getSubtasks();

        assertEquals(0, subtasks.size(), "Список не пустой");
    }

    @Test
    void tasks_epic_DELETE_should_all_tasks_removed() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Удалить все Epic
        removeAllTasksFromManager();
        fillManager(); // наполняем

        URI uri = URI.create(url + "/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Epic> epics = tm.getEpics();

        assertEquals(0, epics.size(), "Список не пустой");

    }

    @Test
    void tasks_task_id_DELETE_should_task_removed() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Удалить Task по id
        removeAllTasksFromManager();
        Task task1 =  addTaskToManager();
        Task task2 =  addTaskToManager();

        URI uri = URI.create(url + "/tasks/task?id=" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Task> tasks = tm.getTasks();

        assertFalse(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
        assertEquals(1, tasks.size(), "Размер списка не соответствует");
    }

    @Test
    void tasks_subtask_id_DELETE_should_task_removed() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Удалить Subtask по id
        removeAllTasksFromManager();
        Epic epic1 = addEpicToManager();
        Subtask task1 =  addSubtaskToManager(epic1);
        Subtask task2 =  addSubtaskToManager(epic1);

        URI uri = URI.create(url + "/tasks/subtask?id=" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Subtask> subtasks = tm.getSubtasks();

        assertFalse(subtasks.contains(task1));
        assertTrue(subtasks.contains(task2));
        assertEquals(1, subtasks.size(), "Размер списка не соответствует");
    }

    @Test
    void tasks_epic_id_DELETE_should_task_removed() throws IOException, InterruptedException {
        server.stop();
        tm = new FileBackedTaskManager(path);
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        //Удалить Epic по id
        removeAllTasksFromManager();
        Epic task1 = addEpicToManager();
        Epic task2 = addEpicToManager();

        URI uri = URI.create(url + "/tasks/epic?id=" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");

        List<Epic> epics = tm.getEpics();

        assertFalse(epics.contains(task1));
        assertTrue(epics.contains(task2));
        assertEquals(1, epics.size(), "Размер списка не соответствует");
    }

    private Task addTaskToManager() {
        Task task = new Task("name_Task", "description_Task", NEW);
        tm.addTask(task);
        return task;
    }

    private Subtask addSubtaskToManager(Epic epic) {
        Subtask subtask = new Subtask("name_Subtask", "description_Subtask", NEW, epic.getId());
        tm.addSubtask(subtask);
        return subtask;
    }

    private Epic addEpicToManager() {
        Epic epic = new Epic("name_Epic", "description_Epic");
        tm.addEpic(epic);
        return epic;
    }

    private void fillManager() {
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
        subtask3.setDuration(timeStep * 2);

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
    }

    private void removeAllTasksFromManager() {
        tm.removeTasks();
        tm.removeSubtasks();
        tm.removeEpics();
    }
}