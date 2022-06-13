public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        /**
        * +++Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
        */
        // Задачи
        Task task1 = new Task("Task_1", "Task_1Task_1Task_1");
        Task task2 = new Task("Task_2", "Task_2Task_2Task_2");
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);

        // Эпикм
        Epic epic1 = new Epic("Epic_1", "Epic_1Epic_1Epic");
        Epic epic2 = new Epic("Epic_2", "Epic_2Epic_2Epic_2");
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);

        // Подзадачи
        Subtask subtask1 = new Subtask("Subtask_1", "Subtask_1Subtask_1Subtask_1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_1Subtask_1Subtask_2", epic1.getId());
        Subtask subtask3 = new Subtask("Subtask_3", "Subtask_3Subtask_3Subtask_3", epic2.getId());
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        inMemoryTaskManager.addSubtask(subtask3);

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
        System.out.println(inMemoryTaskManager.tasks);
        System.out.println(inMemoryTaskManager.epics);
        System.out.println(inMemoryTaskManager.subtasks + "\n");

        /**
        * +++Измените статусы созданных объектов, распечатайте.
        * +++Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
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
        inMemoryTaskManager.updateTask(task1);
        inMemoryTaskManager.updateTask(task2);

        inMemoryTaskManager.updateEpic(epic1);
        inMemoryTaskManager.updateEpic(epic2);

        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);
        inMemoryTaskManager.updateSubtask(subtask3);

        // Еще раз поменяем у эпиков, они уже должны были сами рассчитаться, пробуем изменить
        epic1.setStatus(TaskStatus.NEW);
        epic2.setStatus(TaskStatus.NEW);
        inMemoryTaskManager.updateEpic(epic1);
        inMemoryTaskManager.updateEpic(epic2);

        //Печать
        System.out.println("После изменения статусов\n");
        System.out.println(inMemoryTaskManager.tasks);
        System.out.println(inMemoryTaskManager.epics);
        System.out.println(inMemoryTaskManager.subtasks + "\n");


        /**
        * +++И, наконец, попробуйте удалить одну из задач и один из эпиков.
        */

//        inMemoryTaskManager.removeTaskById(taskId1); // удаляем задачу
//        inMemoryTaskManager.removeSubtaskById(subtaskId1); // удаляем подзадачу
//        inMemoryTaskManager.removeEpicById(epicId2); // удаляем эпик
//
//        System.out.println("После удаления\n");
//        System.out.println(inMemoryTaskManager.tasks);
//        System.out.println(inMemoryTaskManager.epics);
//        System.out.println(inMemoryTaskManager.subtasks + "\n");


        /**
        * ======= Для себя проверки  =======
        * Прежде закоментить выше тест по удалению и раскоментить метод ниже)
        */
          testMySelf(inMemoryTaskManager, taskId2, epicId1, subtaskId2);
    }

    private static void testMySelf(InMemoryTaskManager inMemoryTaskManager, int taskId2, int epicId1, int subtaskId2) {
        /**
         * 2.3 Получение по идентификатору.
         */
        getObjectsById(inMemoryTaskManager, taskId2, epicId1, subtaskId2);

        /**
         * Возвращать последние 10 просмотренных задач
         */
        getHistory(inMemoryTaskManager);

        /**
         *  3.1 Получение списка всех подзадач определённого эпика.
         */
        getSubtasksByEpic(inMemoryTaskManager, epicId1);


        /**
         * 2.2 Удаление всех задач.
         */
        removeObjects(inMemoryTaskManager);

        /**
         *  Проверка автоматическоой смены статуса у эпика
         */
        checkUpdatedStatus(inMemoryTaskManager);
    }

    private static void checkUpdatedStatus(InMemoryTaskManager inMemoryTaskManager) {
        Epic epic3 = new Epic("Epic_3", "Epic_3Epic_3Epic_3");
        inMemoryTaskManager.addEpic(epic3);
        int epicId3 = epic3.getId();

        System.out.println("Создали новый епик проверяем что статус NEW");
        System.out.println(inMemoryTaskManager.getEpicById(epicId3));
        System.out.println("-----------");

        Subtask subtask5 = new Subtask("Subtask_5", "Subtask_5Subtask_5Subtask_5", epicId3);
        inMemoryTaskManager.addSubtask(subtask5);

        System.out.println("Добавили подзадачу(NEW), проверили что статус NEW");
        System.out.println(inMemoryTaskManager.getEpicById(epicId3));
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(epicId3));
        System.out.println("-----------");

        subtask5.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask5);
        System.out.println("Добавили подзадачу(DONE), проверили что статус DONE");
        System.out.println(inMemoryTaskManager.getEpicById(epicId3));
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(epicId3));
        System.out.println("-----------");

        Subtask subtask6 = new Subtask("Subtask_6", "Subtask_6Subtask_6Subtask_6", epicId3);
        inMemoryTaskManager.addSubtask(subtask6);
        subtask6.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask6);

        System.out.println("Добавили подзадачу(DONE) и подзадачу(IN_PROGRESS), проверили что статус IN_PROGRESS");
        System.out.println(inMemoryTaskManager.getEpicById(epicId3));
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(epicId3));
        System.out.println("-----------");
    }

    private static void removeObjects(InMemoryTaskManager inMemoryTaskManager) {
        inMemoryTaskManager.removeTasks();
        System.out.println("После удаления Tasks\n");
        System.out.println(inMemoryTaskManager.tasks);
        System.out.println(inMemoryTaskManager.epics);
        System.out.println(inMemoryTaskManager.subtasks + "\n");

        inMemoryTaskManager.removeSubtasks();
        System.out.println("После удаления Subtasks\n");
        System.out.println(inMemoryTaskManager.tasks);
        System.out.println(inMemoryTaskManager.epics);
        System.out.println(inMemoryTaskManager.subtasks + "\n");

        inMemoryTaskManager.removeEpics();
        System.out.println("После удаления Epics\n");
        System.out.println(inMemoryTaskManager.tasks);
        System.out.println(inMemoryTaskManager.epics);
        System.out.println(inMemoryTaskManager.subtasks + "\n");
    }

    private static void getObjectsById(InMemoryTaskManager inMemoryTaskManager, int taskId2, int epicId1, int subtaskId2) {
        Task task =  inMemoryTaskManager.getTaskById(taskId2);
        Epic epic =  inMemoryTaskManager.getEpicById(epicId1);
        Subtask subtask =  inMemoryTaskManager.getSubtaskById(subtaskId2);
        System.out.println("Получение задач по id (taskId2 = " + taskId2 + ", epicId1 = " + epicId1
                + ", subtaskId2 = " + subtaskId2 + "\n" );
        System.out.println(task);
        System.out.println(epic);
        System.out.println(subtask);
    }

    private static void getHistory(InMemoryTaskManager inMemoryTaskManager) {
        // История последних просмотренных
        System.out.println("История последних просмотренных \n");
        System.out.println(inMemoryTaskManager.getHistory() + "\n");
    }

    private static void getSubtasksByEpic(InMemoryTaskManager inMemoryTaskManager, int epicId1) {
        Subtask subtask2;
        subtask2 = new Subtask("Subtask_4", "Subtask_1Subtask_1Subtask_4", epicId1);
        inMemoryTaskManager.addSubtask(subtask2);
        System.out.println("Получение списка всех подзадач определённого эпика");
        System.out.println("Была добавлена подзадача name = Subtask_4\n");
        System.out.println(inMemoryTaskManager.getSubtasksByEpic(epicId1) + "\n");
    }
}
