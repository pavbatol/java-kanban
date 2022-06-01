import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        /*
        +++Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
        */
        Task task_1 = new Task("Task_1", "Task_1Task_1Task_1");
        manager.createTask(task_1);
        Task task_2 = new Task("Task_2", "Task_2Task_2Task_2");
        manager.createTask(task_2);

        Epic epic_1 = new Epic("Epic_1", "Epic_1Epic_1Epic");
        manager.createEpic(epic_1);
        Subtask subtask_1 = new Subtask("Subtask_1", "Subtask_1Subtask_1Subtask_1", epic_1.getId());
        manager.createSubtask(subtask_1);
        Subtask subtask_2 = new Subtask("Subtask_2", "Subtask_1Subtask_1Subtask_2", epic_1.getId());
        manager.createSubtask(subtask_2);

        Epic epic_2 = new Epic("Epic_2", "Epic_2Epic_2Epic_2");
        manager.createEpic(epic_2);
        Subtask subtask_3 = new Subtask("Subtask_3", "Subtask_3Subtask_3Subtask_3", epic_2.getId());
        manager.createSubtask(subtask_3);

        /*
        +++Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)
        */
        System.out.println(manager.tasks);

        /*
        +++Измените статусы созданных объектов, распечатайте.
        +++Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        */

        //Меняем статусы
        task_1.setStatus(TaskStatus.IN_PROGRESS);
        task_2.setStatus(TaskStatus.DONE);

        epic_1.setStatus(TaskStatus.DONE);
        subtask_1.setStatus(TaskStatus.IN_PROGRESS);
        subtask_2.setStatus(TaskStatus.DONE);

        epic_2.setStatus(TaskStatus.IN_PROGRESS);
        subtask_3.setStatus(TaskStatus.DONE);

        //Обновляем
        manager.updateAnyTask(task_1.getId(), task_1);
        manager.updateAnyTask(task_2.getId(), task_2);

        manager.updateAnyTask(epic_1.getId(), epic_1);
        manager.updateAnyTask(subtask_1.getId(), subtask_1);
        manager.updateAnyTask(subtask_2.getId(), subtask_2);

        manager.updateAnyTask(epic_2.getId(), epic_2);
        manager.updateAnyTask(subtask_3.getId(), subtask_3);

        //Печать
        System.out.println(manager.tasks);

        /*
        ---И, наконец, попробуйте удалить одну из задач и один из эпиков.
        */
        manager.deleteTaskById(task_1.getId());
        manager.deleteTaskById(subtask_1.getId());
        manager.deleteTaskById(epic_2.getId());

        System.out.println(manager.tasks);
    }
}
