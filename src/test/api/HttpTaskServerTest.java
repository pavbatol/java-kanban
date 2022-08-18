package api;

import managers.FileBackedTaskManager;
import managers.HTTPTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.NEW;

class HttpTaskServerTest {
    Path path = Paths.get("resourcesTest", "backTest.csv");
    public final int port = 8080;
    public final String host = "http://localhost";
    public final String url = host +":" + port;
    public int id = -1;
    HttpTaskServer server;
    static HttpClient client;

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    public void beforeEach() {
        id = -1;
        // Наполняем менеджер
        final FileBackedTaskManager tm = new FileBackedTaskManager(path);

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

        // Запускаем сервер
        try {
            server = new HttpTaskServer(tm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

    }

    @AfterEach
    void tearDown() {
        server.stop();
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
}