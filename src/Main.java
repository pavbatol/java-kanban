import api.KVServer;
import managers.HTTPTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Managers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static tasks.TaskStatus.NEW;

public class Main {
    public static void main(String[] args) {
        //Запуск FileBackedTaskManager.main
        //String[] pathElements= new String[]{"resources", "back.csv"};
        //FileBackedTaskManager.main(pathElements);

        //Запуск HTTPTaskManager.main
        //HTTPTaskManager.main(new String[] {"http://localhost:8078"});

        // Test
        KVServer server;
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

        HTTPTaskManager tm = (HTTPTaskManager)Managers.getDefault();

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

        String key = "taskManager";
        HTTPTaskManager tm2 = (HTTPTaskManager) Managers.getDefault();
        tm2 = HTTPTaskManager.loadFromServer(tm2.getClient(), key);
        System.out.println("\nМенеджер оригинальный");
        System.out.println(tm);
        System.out.println("\nМенеджер загруженный с сервера");
        System.out.println(tm2);

        server.stop();

    }

}
