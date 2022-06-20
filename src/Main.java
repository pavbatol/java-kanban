import util.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import managers.TaskManager;

public class Main {
    private static final String LINE_SEPARATOR = "-----------";
    
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        /**
        * Создать 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
        */
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
        Subtask subtask3 = new Subtask("Subtask_3", "Subtask_3Subtask_3Subtask_3", epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        // Для дальнейших проверок соберу новые id (при добавлении менеджер их поменял)
        int taskId1 = task1.getId();
        int taskId2 = task2.getId();
        int epicId1 = epic1.getId();
        int epicId2 = epic2.getId();
        int subtaskId1 = subtask1.getId();
        int subtaskId2 = subtask2.getId();
        int subtaskId3 = subtask3.getId();

        // Распечатаем списки эпиков, задач и подзадач
        System.out.println("После создания объектов");
        printAllListsOfTasks(taskManager);
        printHistory(taskManager);

        /**
        * Изменить статусы созданных объектов. Проверить статусы
        */
        // Установим новые ссылки, чтобы не поменять статусы у оригиналов
        task1 = new Task("Task_1", "Task_1Task_1Task_1");
        task2 = new Task("Task_2", "Task_2Task_2Task_2");
        epic1 = new Epic("Epic_1", "Epic_1Epic_1Epic");
        epic2 = new Epic("Epic_2", "Epic_2Epic_2Epic_2");
        subtask1 = new Subtask("Subtask_1", "Subtask_1Subtask_1Subtask_1", -1);
        subtask2 = new Subtask("Subtask_2", "Subtask_1Subtask_1Subtask_2", -1);
        subtask3 = new Subtask("Subtask_3", "Subtask_3Subtask_3Subtask_3", -1);

        // Проставляем им id задач, которые хотим обновить
        task1.setId(taskId1);
        task2.setId(taskId2);
        epic1.setId(epicId1);
        epic2.setId(epicId2);
        subtask1.setId(subtaskId1);
        subtask2.setId(subtaskId2);
        subtask3.setId(subtaskId3);

        // Меняем статусы
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);
        epic1.setStatus(TaskStatus.DONE);
        epic2.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);

        // Обновляем оригиналы
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        System.out.println("После изменения статусов");
        printAllListsOfTasks(taskManager);
        printHistory(taskManager);

        /**
        * Удалить по id
        */
        taskManager.removeTaskById(taskId1);
        taskManager.removeSubtaskById(subtaskId1);
        taskManager.removeEpicById(epicId2);

        System.out.println("После удаления");
        printAllListsOfTasks(taskManager);
        printHistory(taskManager);

        /**
        * Дополнительные проверки
        * Для полноты картины, можно прежде закомментировать выше тест 'Удалить по id'
        */
          testMySelf(taskManager, taskId2, epicId1, subtaskId2);
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

        System.out.println("Создали новый эпик проверяем что статус NEW (путем просмотра задачи по id)");
        System.out.println(taskManager.getEpicById(epicId3) + "\n" + LINE_SEPARATOR);
        printHistory(taskManager); //История просмотра

        Subtask subtask5 = new Subtask("Subtask_5", "Subtask_5Subtask_5Subtask_5", epicId3);
        taskManager.addSubtask(subtask5);

        System.out.println("Добавили подзадачу (NEW), проверили что статус NEW (путем просмотра задачи по id)");
        printEpicRelations(taskManager, epicId3);
        printHistory(taskManager); //История просмотра

        subtask5.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask5);
        System.out.println("Изменили подзадачу (DONE), проверили что статус DONE (путем просмотра задачи по id)");
        printEpicRelations(taskManager, epicId3);
        printHistory(taskManager); //История просмотра

        Subtask subtask6 = new Subtask("Subtask_6", "Subtask_6Subtask_6Subtask_6", epicId3);
        taskManager.addSubtask(subtask6);
        subtask6.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask6);

        System.out.println("Добавили подзадачу (IN_PROGRESS), проверили что статус IN_PROGRESS (путем просмотра задачи по id)");
        printEpicRelations(taskManager, epicId3);
        printHistory(taskManager); //История просмотра
    }

    private static void removeAllListsOfTasks(TaskManager taskManager) {
        taskManager.removeTasks();
        System.out.println("После удаления Tasks\n");
        printAllListsOfTasks(taskManager);
        printHistory(taskManager); //История просмотра

        taskManager.removeSubtasks();
        System.out.println("После удаления Subtasks\n");
        printAllListsOfTasks(taskManager);
        printHistory(taskManager); //История просмотра

        taskManager.removeEpics();
        System.out.println("После удаления Epics\n");
        printAllListsOfTasks(taskManager);
        printHistory(taskManager); //История просмотра
    }

    private static void printGottenTasksById(TaskManager taskManager, int taskId2, int epicId1, int subtaskId2) {
        Task task =  taskManager.getTaskById(taskId2);
        Epic epic =  taskManager.getEpicById(epicId1);
        Subtask subtask =  taskManager.getSubtaskById(subtaskId2);
        System.out.println("Получение задач по id (taskId2 = " + taskId2 + ", epicId1 = " + epicId1
                + ", subtaskId2 = " + subtaskId2 + ")\n" );
        System.out.println(task);
        System.out.println(epic);
        System.out.println(subtask + "\n" + LINE_SEPARATOR);
        printHistory(taskManager); //История просмотра

    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("История просмотра");
        System.out.println(taskManager.getHistory() + "\n" + LINE_SEPARATOR);
    }

    private static void printGottenSubtasksByEpicId(TaskManager taskManager, int epicId1) {
        Subtask subtask2;
        subtask2 = new Subtask("Subtask_4", "Subtask_1Subtask_1Subtask_4", epicId1);
        taskManager.addSubtask(subtask2);
        System.out.println("Получение списка всех подзадач эпика. Была добавлена подзадача name = Subtask_4");
        System.out.println(taskManager.getSubtasksByEpicId(epicId1) + "\n" + LINE_SEPARATOR);
        printHistory(taskManager); //История просмотра
    }

    private static void printAllListsOfTasks(TaskManager taskManager) {
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks() + "\n" + LINE_SEPARATOR);
    }

    private static void printEpicRelations(TaskManager taskManager, int epicId) {
        System.out.println(taskManager.getEpicById(epicId));
        System.out.println(taskManager.getSubtasksByEpicId(epicId) + "\n" + LINE_SEPARATOR);
    }
}
