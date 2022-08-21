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
import static tasks.TaskStatus.NEW;

class HttpUserServerTest {
    private int userId;
    private User user;
    private Task task;
    private URI uri = URI.create("http://localhost:8080/api/v1/users");
    private final Gson gson = Managers.getGson();
    private static final TaskManager tm = new InMemoryTaskManager();
    private static final UserManager um = new InMemoryUserManager(tm);
    private static final HttpUserServer server;

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
        user = new User("Тестов Тест Тестович");
        userId = um.addUser(user);
        task = new Task(userId, "Task", "TaskTaskTask", NEW);
    }

    @Test
    void getUsers() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type userListType = new TypeToken<ArrayList<User>>(){
        }.getType();

        List<User> users = gson.fromJson(response.body(), userListType);

        users.forEach(System.out::println);

        assertEquals(1, users.size(), "Неверный размер списка");
        assertEquals(user, users.get(0));


    }
}