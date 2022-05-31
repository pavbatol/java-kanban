public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task = new Task(1,"Task_1", "description_1");
        Subtask subtask = new Subtask(1,"Subtask_1", "description_1", 123);
        Epic epic = new Epic(1,"Epic_1", "description_1");

//        // Проверяем добавление для Task
//        manager.createTask(task);
//        manager.createTask("Task_1", "Task description_1", TaskType.TASK);
//
//        // Проверяем добавление для Subtask
//        manager.createTask(subtask);
//        manager.createTask("Subtask_1", "Subtask description_1", TaskType.SUBTASK);
//
//        // Проверяем добавление для Epic
//        manager.createTask(epic);
//        manager.createTask("Epic_1", "Epic description_1", TaskType.EPIC);
//
//        System.out.println(manager.tasks);

        // Проверяем создания для Task
        manager.createTask(task);
        manager.createTask("Task_2", "description_2");

        // Проверяем создания для Subtask
        manager.createSubtask(subtask);
        manager.createSubtask("Subtask_2", "description_2", 123);

        // Проверяем создания для Epic
        manager.createEpic(epic);
        manager.createEpic("Epic_2", "description_2");

        manager.createSubtask("Subtask_2", "description_2", 3);

        System.out.println(manager.tasks);
    }
}
