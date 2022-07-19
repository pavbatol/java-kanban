import managers.FileBackedTasksManager;
import util.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import managers.TaskManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final String LINE_SEPARATOR = "-----------";
    
    public static void main(String[] args) {
//        File file = new File("resources "+ File.separator + "test.csv");
//        Path file = Paths.get("resources", "test.csv");
        Path path = Paths.get("my_test_resources", "aaa", "bbb", "test.csv");
        FileBackedTasksManager taskManager = new FileBackedTasksManager(path);
        FileBackedTasksManager.main(path);

/*        Task task1 = new Task("Name_Task_1", "Description_Task_1");
        Task task2 = new Task("Name_Task_2", "Description_Task_2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Name_Epic_1", "Description_Epic");
        Epic epic2 = new Epic("Name_Epic_2", "Description_Epic_2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Name_Subtask_1", "Description_Subtask_1", epic1.getId());
        Subtask subtask2 = new Subtask("Name_Subtask_2", "Description_Subtask_2", epic1.getId());
        Subtask subtask3 = new Subtask("Name_Subtask_3", "Description_Subtask_3", epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        System.out.println("После создания объектов");
        printAllListsOfTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task1.getId());
        System.out.println("После вызова всех задач в хаотичном порядке и по несколько раз");
        printHistory(taskManager);

        System.out.println("Перевод задач в строку");
        System.out.println(taskManager.toString(task1));
        System.out.println(taskManager.toString(epic1));
        System.out.println(taskManager.toString(subtask1));

        System.out.println(LINE_SEPARATOR +"\nПолучение задачи из строки");
        Task task10 = taskManager.fromString(taskManager.toString(task1)); // + "t"
        System.out.println(task10 != null ? task10.toString() : null);
        Task epic10 = taskManager.fromString(taskManager.toString(epic1)); // + "e"
        System.out.println(epic10 != null ? epic10.toString() : null);
        Task subtask10 = taskManager.fromString(taskManager.toString(subtask1)); // + ",s"
        System.out.println(subtask10 != null ? subtask10.toString() : null);

        System.out.println(LINE_SEPARATOR +"\nПеревод Истроии в строку");
        System.out.println(FileBackedTasksManager.toString(taskManager.historyManager));*/

        /*
        TaskManager taskManager = Managers.getDefault();
        // создайте две задачи, эпик с тремя подзадачами и эпик без подзадач;
        Task task1 = new Task("Task_1", "Task_1Task_1Task_1");
        Task task2 = new Task("Task_2", "Task_2Task_2Task_2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Epic_1", "Epic_1Epic_1Epic");
        Epic epic2 = new Epic("Epic_2", "Epic_2Epic_2Epic_2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask_1", "Subtask_1Subtask_1Subtask_1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_1Subtask_1Subtask_2", epic1.getId());
        Subtask subtask3 = new Subtask("Subtask_3", "Subtask_3Subtask_3Subtask_3", epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        System.out.println("После создания объектов");
        printAllListsOfTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        System.out.println("После вызова всех задач по порядку");
        printHistory(taskManager);

        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        System.out.println("После вызова всех задач в обратном порядке");
        printHistory(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task1.getId());
        System.out.println("После вызова всех задач в хаотичном порядке и по несколько раз");
        printHistory(taskManager);

        taskManager.removeTaskById(task1.getId());
        System.out.println("После удаления задач: Task_1, id = " + task1.getId());
        printHistory(taskManager);

        taskManager.removeEpicById(epic1.getId());
        System.out.println("После удаления задач: Epic_1, id = " + epic1.getId()
                + ", с ним должны удалиться просмотры всех subtask");
        printHistory(taskManager);
        */
    }

    private static void testMySelf(TaskManager taskManager, int taskId2, int epicId1, int subtaskId2) {
        // Получение по идентификатору.
        printGottenTasksById(taskManager, taskId2, epicId1, subtaskId2);

         // Получение списка всех подзадач определённого эпика.
        printGottenSubtasksByEpicId(taskManager, epicId1);

        //Удаление всех задач.
        removeAllListsOfTasks(taskManager);

        //Проверка автоматической смены статуса у эпика
        checkEpicUpdatedStatus(taskManager);
    }

    private static void checkEpicUpdatedStatus(TaskManager taskManager) {
        Epic epic3 = new Epic("Epic_3", "Epic_3Epic_3Epic_3");
        taskManager.addEpic(epic3);
        int epicId3 = epic3.getId();

        System.out.println("Создали новый эпик проверяем что статус NEW (просмотр Эпика по id)");
        System.out.println("\t" +taskManager.getEpicById(epicId3) + "\n" + LINE_SEPARATOR);
        printHistory(taskManager); //История просмотра

        Subtask subtask5 = new Subtask("Subtask_5", "Subtask_5Subtask_5Subtask_5", epicId3);
        taskManager.addSubtask(subtask5);

        System.out.println("Добавили подзадачу (NEW), проверили что статус NEW (просмотр Эпика по id " +
                "и получения всех его подзадач)");
        printEpicRelations(taskManager, epicId3);
        printHistory(taskManager); //История просмотра

        subtask5.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask5);
        System.out.println("Изменили подзадачу (DONE), проверили что статус DONE (просмотр Эпика по id " +
                "и получения всех его подзадач)");
        printEpicRelations(taskManager, epicId3);
        printHistory(taskManager); //История просмотра

        Subtask subtask6 = new Subtask("Subtask_6", "Subtask_6Subtask_6Subtask_6", epicId3);
        taskManager.addSubtask(subtask6);
        subtask6.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask6);

        System.out.println("Добавили подзадачу (IN_PROGRESS), проверили что статус IN_PROGRESS (просмотр Эпика по id " +
                "и получения всех его подзадач)");
        printEpicRelations(taskManager, epicId3);
        printHistory(taskManager); //История просмотра
    }

    private static void removeAllListsOfTasks(TaskManager taskManager) {
        taskManager.removeTasks();
        System.out.println("После удаления Tasks");
        printAllListsOfTasks(taskManager);
        printHistory(taskManager); //История просмотра

        taskManager.removeSubtasks();
        System.out.println("После удаления Subtasks");
        printAllListsOfTasks(taskManager);
        printHistory(taskManager); //История просмотра

        taskManager.removeEpics();
        System.out.println("После удаления Epics");
        printAllListsOfTasks(taskManager);
        printHistory(taskManager); //История просмотра
    }

    private static void printGottenTasksById(TaskManager taskManager, int taskId2, int epicId1, int subtaskId2) {
        Task task =  taskManager.getTaskById(taskId2);
        Epic epic =  taskManager.getEpicById(epicId1);
        Subtask subtask =  taskManager.getSubtaskById(subtaskId2);
        System.out.println("Получение задач по id (taskId2 = " + taskId2 + ", epicId1 = " + epicId1
                + ", subtaskId2 = " + subtaskId2);
        System.out.println("\t" + task);
        System.out.println("\t" + epic);
        System.out.println("\t" + subtask + "\n" + LINE_SEPARATOR);
        printHistory(taskManager); //История просмотра

    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("История просмотра");
        taskManager.getHistory().forEach(task -> System.out.println("\t" + task));
        System.out.println(LINE_SEPARATOR);
    }

    private static void printGottenSubtasksByEpicId(TaskManager taskManager, int epicId1) {
        Subtask subtask2;
        subtask2 = new Subtask("Subtask_4", "Subtask_4Subtask_4Subtask_4", epicId1);
        taskManager.addSubtask(subtask2);
        System.out.println("Получение списка всех подзадач эпика (epicId1 = " + epicId1 + "). Была добавлена подзадача name = Subtask_4");
        taskManager.getSubtasksByEpicId(epicId1).forEach(subtask -> System.out.println("\t" + subtask));
        System.out.println(LINE_SEPARATOR);
        printHistory(taskManager); //История просмотра
    }

    private static void printAllListsOfTasks(TaskManager taskManager) {
        System.out.println("\ttaskManager = " + taskManager.toString().replace("\n", "\n\t")
                + "\n" + LINE_SEPARATOR);
    }

    private static void printEpicRelations(TaskManager taskManager, int epicId) {
        System.out.println("\t" +taskManager.getEpicById(epicId));
        taskManager.getSubtasksByEpicId(epicId).forEach(task -> System.out.println("\t" + task));
        System.out.println(LINE_SEPARATOR);
    }
}
