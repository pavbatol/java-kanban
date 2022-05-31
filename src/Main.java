import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        /*
        2.4 Создание задачи
        По три варианта создания на подачу аргумента:
            1, Новый объект созданный без запроса ID
            2. Новый объект созданный с запросом ID
            3. Без объекта, только значения необходимых полей
        */
//        createTasks(manager); // Подробности в методе

        /*
        +++2.1 Получение списка всех задач по типу.
        */
//        getTasksByType(manager); // Подробности в методе

        /*
        +++2.2 Удаление всех задач по типу.
        */
//        deleteTasksByType(manager); // Подробности в методе

        /*
        +++2.3 Получение по идентификатору.
        */
//        getTaskById(manager); // Подробности в методе

        /*
        ---2.6 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        */


        /*
        +++2.6 Удаление по идентификатору.
        */
//        deleteTaskById(manager, 3);


    }

    private static void deleteTaskById(Manager manager, int id ) {
        System.out.println("\nСписок перед удалением");
        createTasks(manager);
        System.out.println("\nПолучение объекта по ID = " + id);
        System.out.println(manager.getTaskById(id));
        System.out.println("Удаление объекта по ID = " + id);
        manager.deleteTaskById(id);
        System.out.println("Пробуем получить этот объект (ID = " + id  + ") = " + manager.getTaskById(id));
        System.out.println("\nСписок после удаления");
        System.out.println(manager.tasks);
    }

    private static void getTaskById(Manager manager) {
        System.out.println("Получение объекта по ID = 0 ");
        System.out.println(manager.getTaskById(0));

        System.out.println("Получение объекта по ID = 4");
        System.out.println(manager.getTaskById(4));

        System.out.println("Получение объекта по ID = 7");
        System.out.println(manager.getTaskById(7));
    }

    private static void deleteTasksByType(Manager manager) {
        /* Здесь можно закоментить блоки, которые не проверяем,
        чтобы видеть работу по каждому типу */

//        manager.deleteTasksByType(TaskType.EPIC);
//        System.out.println("Удаляем типы EPIC");
//        System.out.println(manager.tasks);
//
//        manager.deleteTasksByType(TaskType.SUBTASK);
//        System.out.println("Удаляем типы SUBTASK");
//        System.out.println(manager.tasks);
//
//        manager.deleteTasksByType(TaskType.TASK);
//        System.out.println("Удаляем типы TASK");
//        System.out.println(manager.tasks);
    }

    private static void getTasksByType(Manager manager) {
        ArrayList<Object> tasksByType = null;

        tasksByType = manager.getTasksByType(TaskType.TASK);
        System.out.println("Задачи по типу TASK");
        System.out.println(tasksByType);

        tasksByType = manager.getTasksByType(TaskType.SUBTASK);
        System.out.println("Задачи по типу SUBTASK");
        System.out.println(tasksByType);

        tasksByType = manager.getTasksByType(TaskType.EPIC);
        System.out.println("Задачи по типу EPIC");
        System.out.println(tasksByType);
    }

    private static void createTasks(Manager manager) {
        // Проверяем создания для Task
        manager.createTask(new Task("Task_0", "description_0"));
        manager.createTask(new Task(-1,"Task_1", "description_1"));
        manager.createTask("Task_2", "description_2");

        // Проверяем создания для Subtask !!! - эти субзадачи не должны создаться - неверный Эпик!!!
//        manager.createSubtask(new Subtask("Subtask_0","description_0", 123));
//        manager.createSubtask(new Subtask(-1,"Subtask_0","description_0", 123));
//        manager.createSubtask("Subtask_2", "description_2", 123);

        // Проверяем создания для Epic
        manager.createEpic(new Epic("Epic_0", "description_0"));
        manager.createEpic(new Epic(-1,"Epic_1", "description_1"));
        manager.createEpic("Epic_2", "description_2");

        // Еще раз создания для Subtask - Здесь уже должна создаться субзадача
        manager.createSubtask(new Subtask("Subtask_0", "description_0", 3));
        manager.createSubtask(new Subtask(-1, "Subtask_1", "description_1", 3));
        manager.createSubtask("Subtask_2", "description_2", 4);

        // Смотрим результаты создания задач разных типов
        System.out.println(manager.tasks);
    }


}
