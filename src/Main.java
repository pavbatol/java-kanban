import api.HttpTaskServer;
import managers.FileBackedTaskManager;
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
        //Запуск FileBackedTaskManager
//        String[] pathElements= new String[]{"resources", "back.csv"};
//        FileBackedTaskManager.main(pathElements);

        FileBackedTaskManager fbtm = Managers.getNewFileBackedTaskManager();

        Epic epic1 = new Epic("name0", "description0");
        Epic epic2 = new Epic("name0", "description0");
        fbtm.addEpic(epic1);
        fbtm.addEpic(epic2);
        Task task1 = new Task("name1", "description1", NEW);
        Task task2 = new Task("name2", "description2", NEW);
        Subtask subtask1 = new Subtask("name4", "description4", NEW, epic1.getId());
        Subtask subtask2 = new Subtask("name5", "description5", NEW, epic1.getId());

        fbtm.addTask(task1);
        fbtm.addTask(task2);
        fbtm.addSubtask(subtask1);
        fbtm.addSubtask(subtask2);

        //System.out.println(fbtm);

        final int timeStep = fbtm.getTimeStepByTimeManager();
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

        fbtm.getTaskById(task1.getId());
        //fbtm.getTaskById(task2.getId());
        fbtm.getSubtaskById(subtask1.getId());
        //fbtm.getSubtaskById(subtask2.getId());

//        System.out.println(fbtm);

//        List<Task> allTasks = fbtm.getTasks();
//        allTasks.addAll(fbtm.getEpics());
//        allTasks.addAll(fbtm.getSubtasks());

        //allTasks.forEach(System.out::println);



        HttpTaskServer httpTaskServer;
        try {
            httpTaskServer = new HttpTaskServer(fbtm);
        } catch (IOException e) {
            System.out.println("Не удалось создать сервер");
            return;
        }
        httpTaskServer.start();




        //httpTaskServer.stop();
    }
}
