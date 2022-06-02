import java.lang.reflect.Array;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        /**
        * +++Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
        */
        // Задачи
        Task task1 = new Task("Task_1", "Task_1Task_1Task_1");
        Task task2 = new Task("Task_2", "Task_2Task_2Task_2");
        manager.addTask(task1);
        manager.addTask(task2);

        // Эпикм
        Epic epic1 = new Epic("Epic_1", "Epic_1Epic_1Epic");
        Epic epic2 = new Epic("Epic_2", "Epic_2Epic_2Epic_2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);


        // Подзадачи
        Subtask subtask1 = new Subtask("Subtask_1", "Subtask_1Subtask_1Subtask_1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_1Subtask_1Subtask_2", epic1.getId());
        Subtask subtask3 = new Subtask("Subtask_3", "Subtask_3Subtask_3Subtask_3", epic2.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        // Для дальнейших проверок соберу новые id
        int taskId1 = task1.getId();
        int taskId2 = task2.getId();
        int epicId1 = epic1.getId();
        int epicId2 = epic2.getId();
        int subtaskId1 = subtask1.getId();
        int subtaskId2 = subtask2.getId();
        int subtaskId3 = subtask3.getId();

        /**
        * +++Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)
        */

        System.out.println("После создания объектов\n");
        System.out.println(manager.tasks);
        System.out.println(manager.epics);
        System.out.println(manager.subtasks + "\n");

        /**
        * +++Измените статусы созданных объектов, распечатайте.
        * +++Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        */

        // Новые ссылки сделаем
        task1 = new Task("Task_1", "Task_1Task_1Task_1");
        task2 = new Task("Task_2", "Task_2Task_2Task_2");

        epic1 = new Epic("Epic_1", "Epic_1Epic_1Epic");
        epic2 = new Epic("Epic_2", "Epic_2Epic_2Epic_2");

        subtask1 = new Subtask("Subtask_1", "Subtask_1Subtask_1Subtask_1", -1);
        subtask2 = new Subtask("Subtask_2", "Subtask_1Subtask_1Subtask_2", -1);
        subtask3 = new Subtask("Subtask_3", "Subtask_3Subtask_3Subtask_3", -1);

        // Меняем статусы
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);

        epic1.setStatus(TaskStatus.DONE);
        epic2.setStatus(TaskStatus.IN_PROGRESS);

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);

        // Обновляем
        manager.updateTask(taskId1, task1);
        manager.updateTask(taskId2, task2);

        manager.updateEpic(epicId1, epic1);
        manager.updateEpic(epicId2, epic2);

        manager.updateSubtask(subtaskId1, subtask1);
        manager.updateSubtask(subtaskId2, subtask2);
        manager.updateSubtask(subtaskId3, subtask3);

        // Еще раз поменяем у эпиков, они уже должны были сами рассчитаться, пробуем изменить
        epic1.setStatus(TaskStatus.NEW);
        epic2.setStatus(TaskStatus.NEW);
        manager.updateEpic(epicId1, epic1);
        manager.updateEpic(epicId2, epic2);

        //Печать
        System.out.println("После изменения статусов\n");
        System.out.println(manager.tasks);
        System.out.println(manager.epics);
        System.out.println(manager.subtasks + "\n");


        /**
        * +++И, наконец, попробуйте удалить одну из задач и один из эпиков.
        */

        manager.removeTaskById(taskId1); // удаляем задачу
        manager.removeSubtaskById(subtaskId1); // удаляем подзадачу
        manager.removeEpicById(epicId2); // удаляем эпик

        System.out.println("После удаления\n");
        System.out.println(manager.tasks);
        System.out.println(manager.epics);
        System.out.println(manager.subtasks + "\n");

        /**
         *  Для себя проверка - Получение списка всех подзадач определённого эпика.
         */

        subtask2 = new Subtask("Subtask_2", "Subtask_1Subtask_1Subtask_2", epicId1);
        manager.addSubtask(subtask2);
        System.out.println("Для себя проверка - Получение списка всех подзадач определённого эпика\n");
        System.out.println(manager.getSubtasksByEpic(epicId1));

    }
}
