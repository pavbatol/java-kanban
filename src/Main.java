public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task = new Task(1,"Task_1", "Task description_1");
        Subtask subtask = new Subtask(1,"Subtask_1", "Subtask description_1", 111);
        Epic epic = new Epic(1,"Epic_1", "Epic description_1");

        // Проверяем добавление для Task
        manager.createTask("Task_1", "Task description_1", TaskType.TASK);
        manager.createTask(task);

        // Проверяем добавление для Subtask
        manager.createTask("Subtask_1", "Subtask description_1", TaskType.SUBTASK);
        manager.createTask(subtask);

        // Проверяем добавление для Epic
        manager.createTask("Epic_1", "Epic description_1", TaskType.EPIC);
        manager.createTask(epic);

        System.out.println(manager.tasks);

//        // Проверяем добавление для Task
//        manager.createTask(new Task());
//        System.out.println(manager.tasks);
//        // Проверяем добавление для Subtask
//        manager.createTask(new Subtask());
//        System.out.println(manager.subtasks);
//        // Проверяем добавление для Epic
//        manager.createTask(new Epic());
//        System.out.println(manager.epics);

//        System.out.println(TaskType.TYPE_SUBTASK);
    }
}
