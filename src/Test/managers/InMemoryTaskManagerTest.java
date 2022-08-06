package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tasks.TaskStatus.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @Override
    protected InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void getPrioritizedTasks() {
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Список не пустой");

        final Task task1 = new Task("Name", "Description", NEW);
        final Task task2 = new Task("Name", "Description", NEW);
        final int id1 = taskManager.addTask(task1);
        final int id2 = taskManager.addTask(task2);
        final Epic epic1 = new Epic("Name", "Description");
        final Epic epic2 = new Epic("Name", "Description");
        final int epicId1 = taskManager.addEpic(epic1);
        final int epicId2 = taskManager.addEpic(epic2);
        final Subtask subtask1 = new Subtask("Name", "Description", NEW, epicId1);
        final Subtask subtask2 = new Subtask("Name", "Description", IN_PROGRESS, epicId1);
        final Subtask subtask3 = new Subtask("Name", "Description", DONE, epicId2);
        final int id3 = taskManager.addSubtask(subtask1);
        final int id4 = taskManager.addSubtask(subtask2);
        final int id5 = taskManager.addSubtask(subtask3);
        taskManager.getTaskById(id1);
        taskManager.getTaskById(id2);
        taskManager.getSubtaskById(id3);
        taskManager.getSubtaskById(id4);
        taskManager.getSubtaskById(id5);

        task1.setDuration(10);
        task1.setStartTime(LocalDateTime.of(2022,8,5,9,0)); // 2
        task2.setDuration(10);
        task2.setStartTime(LocalDateTime.of(2022,8,5,8,0)); // 1
        subtask1.setDuration(10);
        subtask1.setStartTime(LocalDateTime.of(2022,8,5,7,0)); // 0
        subtask2.setDuration(10);
        subtask2.setStartTime(LocalDateTime.of(2022,8,5,11,0)); // 4
        subtask3.setDuration(10);
        subtask3.setStartTime(LocalDateTime.of(2022,8,5,10,0)); // 3

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(5, prioritizedTasks.size(), "Неверный размер списка");
        assertEquals(subtask1, prioritizedTasks.get(0), "Неверный порядок");
        assertEquals(task2, prioritizedTasks.get(1), "Неверный порядок");
        assertEquals(task1, prioritizedTasks.get(2), "Неверный порядок");
        assertEquals(subtask3, prioritizedTasks.get(3), "Неверный порядок");
        assertEquals(subtask2, prioritizedTasks.get(4), "Неверный порядок");

        //taskManager.getPrioritizedTasks().forEach(t -> System.out.println(t + "\n"));
    }

    @Test
    void testToString() {
        assertTrue(taskManager.toString().length() > 0);
    }
}