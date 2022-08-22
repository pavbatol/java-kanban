package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import managers.InMemoryUserManager;
import managers.TaskManager;
import managers.UserManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.User;
import util.Managers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static tasks.TaskStatus.NEW;

class HttpUserServerTest {
    private User user;
    private final URI uri = URI.create("http://localhost:8080/api/v1/users");
    private final Gson gson = Managers.getGson();
    private static final TaskManager tm = new InMemoryTaskManager();
    private static final UserManager um = new InMemoryUserManager(tm);
    private static final HttpUserServer server;
    private final HttpClient client = HttpClient.newHttpClient();

    static {
        try {
            server = new HttpUserServer(um);
        } catch (IOException e) {
            System.out.println("Не удалось создать сервер");
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        server.start();
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @BeforeEach
    void setUp() {
        um.removeUsers();
        tm.removeTasks();
        tm.removeSubtasks();
        tm.removeEpics();
        user = new User("Тестов Тест Тестович");

    }

    @Test
    void getUsers_should_be_received_all_users() throws IOException, InterruptedException {
        um.addUser(user);

        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type userListType = new TypeToken<ArrayList<User>>(){
        }.getType();

        List<User> users = gson.fromJson(response.body(), userListType);

        assertEquals(1, users.size(), "Неверный размер списка");
        assertEquals(user, users.get(0));
    }

    @Test
    void getUser_should__be_received_user_by_id() throws IOException, InterruptedException {
        um.addUser(user);

        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type userListType = new TypeToken<ArrayList<User>>(){
        }.getType();

        List<User> users = gson.fromJson(response.body(), userListType);

        assertEquals(1, users.size(), "Неверный размер списка");
        assertEquals(user, users.get(0));

        //Получаем по id
        int id = users.get(0).getId();
        URI url = URI.create(uri.toString() + "/" + id);
        request = HttpRequest.newBuilder().GET().uri(url).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        User receivedUser = gson.fromJson(response.body(), User.class);

        assertEquals(receivedUser, users.get(0));
    }

    @Test
    void removeUsers_should_be_removed_users() throws IOException, InterruptedException {
        fillUserManager();

        assertEquals(3, um.getUsers().size());

        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, um.getUsers().size());
    }

    @Test
    void removeUser_should_be_removed_user_by_id() throws IOException, InterruptedException {
        um.addUser(user);

        // Проверяем что есть пользователь
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type userListType = new TypeToken<ArrayList<User>>(){
        }.getType();

        List<User> users = gson.fromJson(response.body(), userListType);

        assertEquals(1, users.size(), "Неверный размер списка");
        assertEquals(user, users.get(0));

        //удаляем пользователя
        int id = users.get(0).getId();
        URI url = URI.create(uri.toString() + "/" + id);
        request = HttpRequest.newBuilder().DELETE().uri(url).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        users = gson.fromJson(response.body(), userListType);

        assertNull(users, "Список не null");
    }

    @Test
    void getUserAllTasks_should_be_all_tasks_received() throws IOException, InterruptedException {
        int userId1 = um.addUser(user);
        int userId2 = um.addUser(new User("User"));
        fillTaskManager(userId1);
        List<Task> allTask1 = um.getUserAllTasks(userId1);
        List<Task> allTask2 = um.getUserAllTasks(userId2);

        assertEquals(7, allTask1.size());
        assertEquals(0, allTask2.size());

        URI url = URI.create(String.format( "%s/%s/tasks", uri, userId1));
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        Type taskListType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> allTasks = gson.fromJson(response.body(), taskListType);

        assertEquals(7, allTasks.size(), "Размер списка неверный");

        HttpClient client2 = HttpClient.newHttpClient();
        url = URI.create(String.format( "%s/%s/tasks", uri, userId2));

        request = HttpRequest.newBuilder().GET().uri(url).build();
        response = client2.send(request,HttpResponse.BodyHandlers.ofString());

        allTasks = gson.fromJson(response.body(), taskListType);

        assertEquals(0, allTasks.size(), "Размер списка неверный");
    }

    @Test
    void updateUser_should_be_user_updated() throws IOException, InterruptedException {
        um.addUser(user);
        int id = user.getId();
        User newUser = new User("newName");
        newUser.setId(id);

        URI url = URI.create(String.format("%s/%s", uri, id));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newUser)))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код не совпадает");
        assertEquals(1, um.getUsers().size());
        assertEquals("newName", um.getUser(id).getName());
    }

    @Test
    void updateUser_should_be_user_added() throws IOException, InterruptedException {
        um.addUser(user);
        User newUser = new User("newName");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newUser)))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(um);

        assertEquals(201, response.statusCode(), "Код не совпадает");
        assertEquals(2, um.getUsers().size());
        assertEquals("newName", um.getUser(1).getName());
    }


    private void fillUsersAndTasks() {
        User user1 = new User("Тестов_1 Тест Тестович");
        User user2 = new User("Тестов_2 Тест Тестович");
        User user3 = new User("Тестов_3 Тест Тестович");
        int userId1 = um.addUser(user1);
        int userId2 = um.addUser(user2);
        int userId3 = um.addUser(user3);
        fillTaskManager(userId1);
        fillTaskManager(userId2);
        fillTaskManager(userId3);
    }

    private void fillUserManager() {
        User user1 = new User("Тестов_1 Тест Тестович");
        User user2 = new User("Тестов_2 Тест Тестович");
        User user3 = new User("Тестов_3 Тест Тестович");
        um.addUser(user1);
        um.addUser(user2);
        um.addUser(user3);
    }

    private void fillTaskManager(int userId) {
        Epic epic1 = new Epic(userId, "Epic_1", "EpicEpicEpic");
        Epic epic2 = new Epic(userId, "Task_1", "TaskTaskTask");
        Task task1 = new Task(userId, "Task_1", "TaskTaskTask", NEW);
        Task task2 = new Task(userId, "Task_2", "TaskTaskTask", NEW);
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        tm.addTask(task1);
        tm.addTask(task2);
        Subtask subtask1 = new Subtask(userId, "Subtask_1", "SubtaskSubtaskSubtask",
                NEW, epic1.getId());
        Subtask subtask2 = new Subtask(userId, "Subtask_2", "SubtaskSubtaskSubtask",
                NEW, epic1.getId());
        Subtask subtask3 = new Subtask(userId, "Subtask_3", "SubtaskSubtaskSubtask",
                NEW, epic1.getId());
        tm.addSubtask(subtask1);
        tm.addSubtask(subtask2);
        tm.addSubtask(subtask3);

    }



}