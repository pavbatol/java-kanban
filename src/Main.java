import api.GsonAdapters.InMemoryHistoryManagerAdapter;
import api.GsonAdapters.LocalDateTimeAdapter;
import api.GsonAdapters.PathAdapter;
import api.GsonAdapters.TimeManagerAdapter;
import api.HttpTaskServer;
import api.KVServer;
import api.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.FileBackedTaskManager;
import managers.HTTPTaskManager;
import managers.InMemoryHistoryManager;
import managers.TimeManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Managers;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static tasks.TaskStatus.NEW;

public class Main {
    public static void main(String[] args) throws IOException {
        //Запуск FileBackedTaskManager
//        String[] pathElements= new String[]{"resources", "back.csv"};
//        FileBackedTaskManager.main(pathElements);

        //Запуск HTTPTaskManager
        HTTPTaskManager.main(new String[] {"http://localhost:8078"});


/*
        FileBackedTaskManager fbtm1 = Managers.getNewFileBackedTaskManager();

        Epic epic1 = new Epic("name0", "description0");
        Epic epic2 = new Epic("name0", "description0");
        fbtm1.addEpic(epic1);
        fbtm1.addEpic(epic2);
        Task task1 = new Task("name1", "description1", NEW);
        Task task2 = new Task("name2", "description2", NEW);
        Subtask subtask1 = new Subtask("name4", "description4", NEW, epic1.getId());
        Subtask subtask2 = new Subtask("name5", "description5", NEW, epic1.getId());

        final int timeStep = fbtm1.getTimeStepByTimeManager();
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

        fbtm1.addTask(task1);
        fbtm1.addTask(task2);
        fbtm1.addSubtask(subtask1);
        fbtm1.addSubtask(subtask2);

        fbtm1.getTaskById(task1.getId());
        //fbtm1.getTaskById(task2.getId());
        fbtm1.getSubtaskById(subtask1.getId());
        //fbtm1.getSubtaskById(subtask2.getId());

        HttpTaskServer httpTaskServer;
        try {
            httpTaskServer = new HttpTaskServer(fbtm1);
        } catch (IOException e) {
            System.out.println("Не удалось создать сервер");
            return;
        }
        //httpTaskServer.start();
        //httpTaskServer.stop();

        //----------------

        KVServer server = null;
        KVTaskClient client = null;
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
        }
        try {
            client = new KVTaskClient("http://localhost:8078");
        } catch (RuntimeException e) {
            System.out.println("Не удалось запустить HTTP-Client\n" + e.getMessage());
        }



        System.out.println("Менеджер-оригинал");
        System.out.println(fbtm1);

        Gson gsonForSave = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeHierarchyAdapter(Path.class, new PathAdapter())
                .registerTypeAdapter(InMemoryHistoryManager.class, new InMemoryHistoryManagerAdapter())
                .registerTypeAdapter(TimeManager.class, new TimeManagerAdapter())
                .create();

        String json;
        json = gsonForSave.toJson(fbtm1);
//        json = gsonForSave.toJson(task1);
//        json = gsonForSave.toJson(LocalDateTime.now());
//        json = gsonForSave.toJson(new Task("aaa", "bbb", NEW));
        System.out.println(json);




        if (client != null) {
            FileBackedTaskManager fbtm2 = null;
            String key1 = "put1";
            client.put(key1, json); // Отправляем на сохранение
            String response = client.load(key1); // Запрашиваем для восстановления

//            fbtm2 = gsonForSave.fromJson(response, FileBackedTaskManager.class);
//            System.out.println("Менеджер из восстановления");
//            System.out.println(fbtm2);
        }





//        Gson gsonObject = new GsonBuilder()
//                .setExclusionStrategies(new ExclusionStrategy() {
//                    boolean method shouldSkipClass(Class<?> clz)
//                    boolean method shouldSkipField(FieldAttributes field)
//                });
*/


    }


}
