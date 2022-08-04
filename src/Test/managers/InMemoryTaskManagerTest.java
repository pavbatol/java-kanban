package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.IN_PROGRESS;
import static tasks.TaskStatus.NEW;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addTask() {
        Task task = new Task("Name", "Description", NEW);
        final int id = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(id);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addSubtask() {
        // Нет эпика
        Subtask subtask = new Subtask("Name", "Description", NEW, 0);
        int id = taskManager.addSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtaskById(id);

        assertNull(savedSubtask, "Задача найдена.");

        //есть эпик
        final Epic epic = new Epic("Name", "Description");
        taskManager.addEpic(epic);
        subtask = new Subtask("Name", "Description", NEW, epic.getId());
        id = taskManager.addSubtask(subtask);
        savedSubtask = taskManager.getSubtaskById(id);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(savedSubtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpic() {
        final Epic epic = new Epic("Name", "Description");
        final int id = taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(id);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(savedEpic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        final Task task = new Task("Name", "Description", NEW);
        final int id = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(id);
        final Task newTask = new Task("Name", "Description", NEW);

        newTask.setId(id + 1);
        newTask.setName("newName");
        newTask.setDescription("newDescription");
        newTask.setStatus(IN_PROGRESS);
        newTask.setDuration(task.getDuration() + 10);
        newTask.setStartTime(LocalDateTime.now());

        taskManager.updateTask(newTask);
        Task updatedTask = taskManager.getTaskById(id);

        assertNotEquals(newTask, updatedTask); // был установлен несуществующий id

        newTask.setId(id);
        taskManager.updateTask(newTask);
        updatedTask = taskManager.getTaskById(id);

        assertNotNull(updatedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask, updatedTask, "Задачи не совпадают.");
    }

    @Test
    void updateSubtask() {
    }

    @Test
    void updateEpic() {
    }

    @Test
    void removeTaskById() {
    }

    @Test
    void removeSubtaskById() {
    }

    @Test
    void removeEpicById() {
    }

    @Test
    void removeTasks() {
    }

    @Test
    void removeSubtasks() {
    }

    @Test
    void removeEpics() {
    }

    @Test
    void getTaskById() {
    }

    @Test
    void getSubtaskById() {
    }

    @Test
    void getEpicById() {
    }

    @Test
    void getTasks() {
    }

    @Test
    void getSubtasks() {
    }

    @Test
    void getEpics() {
    }

    @Test
    void getSubtasksByEpicId() {
    }

    @Test
    void getHistory() {
    }

    @Test
    void testToString() {
    }
}