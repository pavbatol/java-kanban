package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.*;

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

        // Есть эпик
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

        // Статус эпика (на начало в наличии одна подзадача - savedSubtask)
        Subtask subtask2 = new Subtask("Name", "Description", NEW, epic.getId());
        taskManager.addSubtask(subtask2);

        assertEquals(NEW, epic.getStatus(), "Неверный статус эпика");

        savedSubtask.setStatus(DONE);
        subtask2.setStatus(DONE);
        Subtask subtask3 = new Subtask("Name", "Description", DONE, epic.getId());
        taskManager.addSubtask(subtask3);

        assertEquals(DONE, epic.getStatus(), "Неверный статус эпика");

        Subtask subtask4 = new Subtask("Name", "Description", NEW, epic.getId());
        taskManager.addSubtask(subtask4);

        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");

        savedSubtask.setStatus(IN_PROGRESS);
        subtask2.setStatus(IN_PROGRESS);
        subtask3.setStatus(IN_PROGRESS);
        subtask4.setStatus(IN_PROGRESS);
        Subtask subtask5 = new Subtask("Name", "Description", IN_PROGRESS, epic.getId());
        taskManager.addSubtask(subtask5);

        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");
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

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask, updatedTask, "Задачи не совпадают."); // ссылки у задач разные
    }

    @Test
    void updateSubtask() {
        final Epic epic = new Epic("Name", "Description");
        final int epicId = taskManager.addEpic(epic);
        final Subtask subtask = new Subtask("Name", "Description", NEW, epicId);
        final int id = taskManager.addSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(id);
        final Subtask newSubtask = new Subtask("Name", "Description", NEW, epicId);

        newSubtask.setId(id + 1);
        newSubtask.setName("newName");
        newSubtask.setDescription("newDescription");
        newSubtask.setStatus(IN_PROGRESS);
        newSubtask.setDuration(subtask.getDuration() + 10);
        newSubtask.setStartTime(LocalDateTime.now());

        taskManager.updateSubtask(newSubtask);
        Subtask updatedSubtask = taskManager.getSubtaskById(id);

        assertNotEquals(newSubtask, updatedSubtask); // был установлен несуществующий id

        newSubtask.setId(id);
        taskManager.updateSubtask(newSubtask);
        updatedSubtask = taskManager.getSubtaskById(id);

        assertNotNull(updatedSubtask, "Задача не найдена.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtask, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(newSubtask, updatedSubtask, "Задачи не совпадают."); // ссылки у задач разные
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