package managers;

import api.KVServer;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasks.TaskStatus.*;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager>{

    public final int port = 8078;
    public final String host = "http://localhost";
    public final String url = host +":" + port;
    final String key = "taskManager";
    static KVServer server;
    @Override
    protected HTTPTaskManager getNewTaskManager() {
        return new HTTPTaskManager(url);
    }

    @BeforeAll
    public static void serverStart() {
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
        }
    }

    @AfterAll
    public static void serverStop() {
        server.stop();
    }

    @Test
    void loadFromServer_should_be_not_loaded_if_no_file_on_server() {
        HTTPTaskManager tm2 = new HTTPTaskManager(url);
        tm2 = HTTPTaskManager.loadFromServer(tm2.getClient(), key);

        assertNull(tm2);
    }

    @Test
    void loadFromServer_should_loaded_if_file_on_server() {
        taskManager = getNewTaskManager();

        Task task1 = new Task("Name", "Description", NEW);
        Task task2 = new Task("Name", "Description", NEW);
        Epic epic1 = new Epic("Name", "Description");
        Epic epic2 = new Epic("Name", "Description"); // пустой эпик
        int epicId1 = taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Name", "Description", NEW, epicId1);
        Subtask subtask2 = new Subtask("Name", "Description", IN_PROGRESS, epicId1);
        Subtask subtask3 = new Subtask("Name", "Description", DONE, epicId1);

        // Проверка с задачами, но без истории
        final int id1 = taskManager.addTask(task1);
        int timeStep = taskManager.getTimeManager().getTimeStep();
        task1.setDuration(timeStep * 5L);
        task1.setStartTime(LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0,
                0));
        taskManager.updateTask(task1);

        final int id2 = taskManager.addTask(task2);
        final int id3 = taskManager.addSubtask(subtask1);
        final int id4 = taskManager.addSubtask(subtask2);
        final int id5 = taskManager.addSubtask(subtask3);

        HTTPTaskManager tm2 = new HTTPTaskManager(url);
        tm2 = HTTPTaskManager.loadFromServer(tm2.getClient(), key);
        //System.out.println("\n-------\n1-ый менеджер:\n" + taskManager);
        //System.out.println("\n-------\n2*ой менеджер:\n" + tm2);

        assertNotNull(tm2);
        assertArrayEquals(taskManager.getTasks().toArray(), tm2.getTasks().toArray(),
                "Списки задач не равны");
        assertArrayEquals(taskManager.getSubtasks().toArray(), tm2.getSubtasks().toArray(),
                "Списки подзадач не равны");
        assertArrayEquals(taskManager.getEpics().toArray(), tm2.getEpics().toArray(),
                "Списки эпиков не равны");
        assertArrayEquals(taskManager.getHistory().toArray(), tm2.getHistory().toArray(),
                "Списки истории не равны");
        assertEquals(0, tm2.getHistory().size(), "Есть история");

        // Проверка с задачами и историей
        taskManager.getTaskById(id1);
        taskManager.getTaskById(id2);
        taskManager.getSubtaskById(id3);
        taskManager.getSubtaskById(id4);
        taskManager.getSubtaskById(id5);

        tm2 = new HTTPTaskManager(url);
        tm2 = HTTPTaskManager.loadFromServer(tm2.getClient(), key);
        //System.out.println("\n-------\n1-ый менеджер:\n" + taskManager);
        //System.out.println("\n-------\n2*ой менеджер:\n" + tm2);

        assertNotNull(tm2);
        assertArrayEquals(taskManager.getTasks().toArray(), tm2.getTasks().toArray(),
                "Списки задач не равны");
        assertArrayEquals(taskManager.getSubtasks().toArray(), tm2.getSubtasks().toArray(),
                "Списки подзадач не равны");
        assertArrayEquals(taskManager.getEpics().toArray(), tm2.getEpics().toArray(),
                "Списки эпиков не равны");
        assertArrayEquals(taskManager.getHistory().toArray(), tm2.getHistory().toArray(),
                "Списки истории не равны");
        assertEquals(5, tm2.getHistory().size(), "Неверный размер списка истории");
    }

    @Test
    void save_should_be_saved() {
        serverStart();
        taskManager = getNewTaskManager();

        taskManager.save();

        HTTPTaskManager tm2 = new HTTPTaskManager(url);
        tm2 = HTTPTaskManager.loadFromServer(tm2.getClient(), key);

        assertNotNull(tm2);
        assertEquals(taskManager, tm2, "Менеджеры не равны");

        serverStop();
    }

}
