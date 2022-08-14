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
        //String[] pathElements= new String[]{"resources", "back.csv"};
        //FileBackedTaskManager.main(pathElements);

        FileBackedTaskManager fbtm = Managers.getNewFileBackedTaskManager();

        Epic epic = new Epic("name0", "description0");
        fbtm.addEpic(epic);
        Task task1 = new Task("name1", "description1", NEW);
        Task task2 = new Task("name2", "description2", NEW);
        Subtask subtask1 = new Subtask("name4", "description4", NEW, epic.getId());
        Subtask subtask2 = new Subtask("name5", "description5", NEW, epic.getId());

        fbtm.addTask(task1);
        fbtm.addTask(task2);
        fbtm.addSubtask(subtask1);
        fbtm.addSubtask(subtask2);

        System.out.println(fbtm);

        final int timeStep = fbtm.getTimeStepByTimesManager();
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

//        fbtm.updateTask(task1);
//        fbtm.updateTask(task2);
//        fbtm.updateSubtask(subtask1);
//        fbtm.updateSubtask(subtask2);

        System.out.println(fbtm);

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
