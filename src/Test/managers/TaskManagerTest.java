package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasks.TaskStatus.*;
import static tasks.TaskStatus.NEW;
import static tasks.TaskType.SUBTASK;
import static tasks.TaskType.TASK;
import static util.Functions.getAnyTypeTaskById;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    protected abstract T getNewTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = getNewTaskManager();
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

        // Статус эпика
        testEpicStatusForSubtaskAdd();
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
        final Task newTask = new Task("Name", "Description", NEW);

        newTask.setId(id + 1);
        newTask.setName("newName");
        newTask.setDescription("newDescription");
        newTask.setStatus(IN_PROGRESS);
        newTask.setDuration(task.getDuration() + 30);
        newTask.setStartTime(LocalDateTime.now());

        taskManager.updateTask(newTask);
        Task updatedTask = taskManager.getTaskById(id);

        assertNotEquals(newTask, updatedTask); // тест на неверный id/пустой список

        newTask.setId(id);
        taskManager.updateTask(newTask);
        updatedTask = taskManager.getTaskById(id);

        assertNotNull(updatedTask, "Задача не найдена.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask, updatedTask, "Задачи не совпадают."); // тест на update ссылки у задач разные
        // Валидность полей времени
        testTimesForUpdateTaskAndSubtaskType(getNewTaskManager(), TASK);
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Name", "Description");
        int epicId = taskManager.addEpic(epic);
        final Subtask subtask = new Subtask("Name", "Description", NEW, epicId);
        final int id = taskManager.addSubtask(subtask);
        Subtask newSubtask = new Subtask("Name", "Description", NEW, epicId);

        newSubtask.setId(id + 1);
        newSubtask.setName("newName");
        newSubtask.setDescription("newDescription");
        newSubtask.setStatus(IN_PROGRESS);
        newSubtask.setDuration(subtask.getDuration() + 10);
        newSubtask.setStartTime(LocalDateTime.now());

        taskManager.updateSubtask(newSubtask);
        Subtask updatedSubtask = taskManager.getSubtaskById(id);

        assertNotEquals(newSubtask, updatedSubtask); // тест на неверный id/пустой список

        newSubtask.setId(id);
        taskManager.updateSubtask(newSubtask);
        updatedSubtask = taskManager.getSubtaskById(id);

        assertNotNull(updatedSubtask, "Задача не найдена.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtask, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(newSubtask, updatedSubtask, "Задачи не совпадают.");//тест на update ссылки у задач разные
        // Статус эпика
        testEpicStatusForSubtaskUpdate();
        // Валидность полей времени
        testTimesForUpdateTaskAndSubtaskType(getNewTaskManager(), SUBTASK);
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Name", "Description");
        Epic newEpic = new Epic("newName", "newDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Name1", "Description1", NEW, epicId);
        Subtask subtask2 = new Subtask("Name2", "Description2", DONE, epicId);
        Subtask subtask3 = new Subtask("Name3", "Description3", IN_PROGRESS, epicId);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        newEpic.setId(epicId + 100); // неверный id

        taskManager.updateEpic(newEpic);
        assertNotEquals(newEpic.getName(), epic.getName(), "Имя равны");

        newEpic.setId(epicId); // верный id
        newEpic.setStartTime(LocalDateTime.now()); // эти поля не поменяются
        newEpic.setEndTime(LocalDateTime.now().plusMinutes(20));
        newEpic.setDuration(20);
        newEpic.setStatus(DONE);

        taskManager.updateEpic(newEpic);
        assertNotEquals(newEpic.getStartTime(), epic.getStartTime(), "Время старта равно");
        assertNotEquals(newEpic.getEndTime(), epic.getEndTime(), "Время окончания равно");
        assertNotEquals(newEpic.getDuration(), epic.getDuration(), "Продолжительность равны");
        assertNotEquals(newEpic.getStatus(), epic.getStatus(), "Статусы равны");
        assertNotEquals(newEpic.getSubtaskIds().size(), epic.getSubtaskIds().size(), "Размер массива равны");

        assertEquals(newEpic.getId(), epic.getId(), "id не равны");
        assertEquals(newEpic.getName(), epic.getName(), "Имя не равны");
        assertEquals(newEpic.getDescription(), epic.getDescription(), "Описание не равны");
    }

    @Test
    void removeTaskById() {
        final Task task1 = new Task("Name", "Description", NEW);
        final Task task2 = new Task("Name", "Description", NEW);
        final Task task3 = new Task("Name", "Description", NEW);
        taskManager.addTask(task1);
        final int id2 = taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.removeTaskById(id2 + 100); // неверный id
        assertEquals(3, taskManager.getTasks().size(), "Размер списка изменился");

        taskManager.removeTaskById(id2);
        assertEquals(2, taskManager.getTasks().size(), "Размер не изменился");
    }

    @Test
    void removeSubtaskById() {
        Epic epic = new Epic("Name", "Description");
        int epicId = taskManager.addEpic(epic);
        final Subtask task1 = new Subtask("Name", "Description", NEW, epicId);
        final Subtask task2 = new Subtask("Name", "Description", IN_PROGRESS, epicId);
        final Subtask task3 = new Subtask("Name", "Description", DONE, epicId);
        final int id1 = taskManager.addSubtask(task1);
        final int id2 = taskManager.addSubtask(task2);
        final int id3 = taskManager.addSubtask(task3);
        task1.setStartTime(LocalDateTime.of(2022,8,5,12,0));
        task2.setStartTime(LocalDateTime.of(2022,8,5,15,0));
        task3.setStartTime(LocalDateTime.of(2022,8,5,18,0));
        task1.setDuration(10);
        task2.setDuration(10);
        task3.setDuration(10);
        int idForDel;

        assertEquals(3, epic.getSubtaskIds().size(), "Размер списка не равен");

        idForDel = id2 + 100;
        taskManager.removeSubtaskById(idForDel); // неверный id
        assertEquals(3, taskManager.getSubtasks().size(), "Размер getSubtasks изменился");

        idForDel = id2;
        taskManager.removeSubtaskById(idForDel);
        assertEquals(2, taskManager.getSubtasks().size(), "Размер getSubtasks не изменился");
        assertEquals(2, epic.getSubtaskIds().size(), "Размер getSubtaskIds не изменился");
        assertNull(taskManager.getSubtaskById(idForDel), "Задача получена");
        assertFalse(epic.getSubtaskIds().contains(idForDel), "Эпик содержит значение: " + idForDel);
        assertEquals(IN_PROGRESS, epic.getStatus());
        assertEquals("2022-08-05T12:00", epic.getStartTime().toString(), "Время start в Эпике неверно");
        assertEquals("2022-08-05T18:10", epic.getEndTime().toString(), "Время end в Эпике неверно");

        idForDel = id1;
        taskManager.removeSubtaskById(idForDel);
        assertEquals(1, taskManager.getSubtasks().size(), "Размер списка не изменился");
        assertEquals(1, epic.getSubtaskIds().size(), "Размер getSubtaskIds не изменился");
        assertNull(taskManager.getSubtaskById(idForDel), "Задача получена");
        assertFalse(epic.getSubtaskIds().contains(idForDel), "Эпик содержит значение: " + idForDel);
        assertEquals(DONE, epic.getStatus());
        assertEquals("2022-08-05T18:00", epic.getStartTime().toString(), "Время start в Эпике неверно");
        assertEquals("2022-08-05T18:10", epic.getEndTime().toString(), "Время end в Эпике неверно");

        idForDel = id3;
        taskManager.removeSubtaskById(idForDel);
        assertEquals(0, taskManager.getSubtasks().size(), "Размер getSubtasks не изменился");
        assertEquals(0, epic.getSubtaskIds().size(), "Размер getSubtaskIds не изменился");
        assertNull(taskManager.getSubtaskById(idForDel), "Задача получена");
        assertFalse(epic.getSubtaskIds().contains(idForDel), "Эпик содержит значение: " + idForDel);
        assertEquals(NEW, epic.getStatus());
        assertNull(epic.getStartTime(), "Время start в Эпике неверно");
        assertNull(epic.getEndTime(), "Время end в Эпике неверно");
    }

    @Test
    void removeEpicById() {
        Epic epic1 = new Epic("Name", "Description");
        Epic epic2 = new Epic("Name", "Description");
        int epicId1 = taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        final Subtask task1 = new Subtask("Name", "Description", NEW, epicId1);
        final Subtask task2 = new Subtask("Name", "Description", IN_PROGRESS, epicId1);
        final Subtask task3 = new Subtask("Name", "Description", DONE, epicId1);
        final int id1 = taskManager.addSubtask(task1);
        final int id2 = taskManager.addSubtask(task2);
        final int id3 = taskManager.addSubtask(task3);

        assertEquals(3, taskManager.getSubtasks().size(), "Неверный размер списка подзадач");
        assertNotNull(taskManager.getSubtaskById(id1));
        assertNotNull(taskManager.getSubtaskById(id2));
        assertNotNull(taskManager.getSubtaskById(id3));

        taskManager.removeEpicById(epicId1 + 100); // неверный id
        assertEquals(2, taskManager.getEpics().size(), "Размер списка изменился");

        taskManager.removeEpicById(epicId1);
        assertEquals(1, taskManager.getEpics().size(), "Размер не изменился");

        assertNull(taskManager.getSubtaskById(id1));
        assertNull(taskManager.getSubtaskById(id2));
        assertNull(taskManager.getSubtaskById(id2));
    }

    @Test
    void removeTasks() {
        final Task task1 = new Task("Name", "Description", NEW);
        final Task task2 = new Task("Name", "Description", NEW);
        final Task task3 = new Task("Name", "Description", NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        assertEquals(3, taskManager.getTasks().size(), "Неверный размер списка");

        taskManager.removeTasks();

        assertEquals(0, taskManager.getTasks().size(), "Задачи не удалены");
    }

    @Test
    void removeSubtasks() {
        Epic epic = new Epic("Name", "Description");
        int epicId = taskManager.addEpic(epic);
        final Subtask task1 = new Subtask("Name", "Description", NEW, epicId);
        final Subtask task2 = new Subtask("Name", "Description", IN_PROGRESS, epicId);
        final Subtask task3 = new Subtask("Name", "Description", DONE, epicId);
        taskManager.addSubtask(task1);
        taskManager.addSubtask(task2);
        taskManager.addSubtask(task3);
        task1.setStartTime(LocalDateTime.of(2022,8,5,12,0));
        task2.setStartTime(LocalDateTime.of(2022,8,5,15,0));
        task3.setStartTime(LocalDateTime.of(2022,8,5,18,0));
        task1.setDuration(10);
        task2.setDuration(10);
        task3.setDuration(10);
        taskManager.updateSubtask(task3); // чтобы рассчитался статус и время у эпика

        assertEquals(3, epic.getSubtaskIds().size(), "Размер списка не равен");

        taskManager.removeSubtasks();
        assertEquals(0, epic.getSubtaskIds().size(), "Задачи не удалены");
        assertEquals(NEW, epic.getStatus(), "Неверный статус эпика");
    }

    @Test
    void removeEpics() {
        Epic epic1 = new Epic("Name", "Description");
        int epicId1 = taskManager.addEpic(epic1);
        final Subtask task1 = new Subtask("Name", "Description", NEW, epicId1);
        final Subtask task2 = new Subtask("Name", "Description", IN_PROGRESS, epicId1);
        final Subtask task3 = new Subtask("Name", "Description", DONE, epicId1);
        taskManager.addSubtask(task1);
        taskManager.addSubtask(task2);
        taskManager.addSubtask(task3);

        assertEquals(1, taskManager.getEpics().size(), "Размер списка не равен");
        assertEquals(3, taskManager.getSubtasks().size(), "Размер списка не равен");

        taskManager.removeEpics();

        assertEquals(0, taskManager.getEpics().size(), "Задачи не удалены");
        assertEquals(0, taskManager.getSubtasks().size(), "Задачи не удалены");
    }

    @Test
    void getTaskById() {
        final Task task = new Task("Name", "Description", NEW);
        final int id = taskManager.addTask(task);

        Task savedTask = taskManager.getTaskById(id + 100);
        assertNull(savedTask, "Задача получена");

        savedTask = taskManager.getTaskById(id);
        assertNotNull(savedTask, "Задача не получена");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void getSubtaskById() {
        final Epic epic = new Epic("Name", "Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Name", "Description", NEW, epic.getId());
        int id = taskManager.addSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtaskById(id + 100);
        assertNull(savedSubtask, "Задача найдена.");

        savedSubtask = taskManager.getSubtaskById(id);
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Name", "Description");
        int id = taskManager.addEpic(epic);

        Epic savedEpic = taskManager.getEpicById(id + 100);
        assertNull(savedEpic, "Задача найдена.");

        savedEpic = taskManager.getEpicById(id);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
    }

    @Test
    void getTasks() {
        assertEquals(0, taskManager.getTasks().size(), "Неверный размер списка");

        final Task task1 = new Task("Name", "Description", NEW);
        final Task task2 = new Task("Name", "Description", NEW);
        int id1 = taskManager.addTask(task1);
        int id2 = taskManager.addTask(task2);

        assertNotNull(taskManager.getTasks(), "Список не получен");
        assertEquals(2, taskManager.getTasks().size(), "Размер списка не верный");
        assertEquals(task1, taskManager.getTaskById(id1), "Задачи не равны");
        assertEquals(task2, taskManager.getTaskById(id2), "Задачи не равны");
    }

    @Test
    void getSubtasks() {
        assertEquals(0,taskManager.getSubtasks().size(), "Неверный размер списка");

        Epic epic = new Epic("Name", "Description");
        int epicId = taskManager.addEpic(epic);
        final Subtask task1 = new Subtask("Name", "Description", NEW, epicId);
        final Subtask task2 = new Subtask("Name", "Description", IN_PROGRESS, epicId);
        int id1 = taskManager.addSubtask(task1);
        int id2 = taskManager.addSubtask(task2);

        assertNotNull(taskManager.getSubtasks(), "Список не получен");
        assertEquals(2, taskManager.getSubtasks().size(), "Размер списка не верный");
        assertEquals(task1, taskManager.getSubtaskById(id1), "Задачи не равны");
        assertEquals(task2, taskManager.getSubtaskById(id2), "Задачи не равны");
    }

    @Test
    void getEpics() {
        assertEquals(0,taskManager.getEpics().size(), "Неверный размер списка");

        final Epic task1 = new Epic("Name", "Description");
        final Epic task2 = new Epic("Name", "Description");
        int id1 = taskManager.addEpic(task1);
        int id2 = taskManager.addEpic(task2);

        assertNotNull(taskManager.getEpics(), "Список не получен");
        assertEquals(2, taskManager.getEpics().size(), "Размер списка не верный");
        assertEquals(task1, taskManager.getEpicById(id1), "Задачи не равны");
        assertEquals(task2, taskManager.getEpicById(id2), "Задачи не равны");
    }

    @Test
    void getSubtasksByEpicId() {
        assertNull(taskManager.getSubtasksByEpicId(0), "Список получен");

        Epic epic1 = new Epic("Name1", "Description1");
        Epic epic2 = new Epic("Name2", "Description2");
        int epicId1 = taskManager.addEpic(epic1);
        int epicId2 = taskManager.addEpic(epic2);

        assertNotNull(taskManager.getSubtasksByEpicId(epicId1), "Список не получен");
        assertNotNull(taskManager.getSubtasksByEpicId(epicId2), "Список не получен");

        final Subtask task1 = new Subtask("Name", "Description", NEW, epicId1);
        final Subtask task2 = new Subtask("Name", "Description", IN_PROGRESS, epicId1);
        taskManager.addSubtask(task1);
        taskManager.addSubtask(task2);
        final Subtask task3 = new Subtask("Name", "Description", IN_PROGRESS, epicId2);
        taskManager.addSubtask(task3);

        assertNotNull(taskManager.getSubtasksByEpicId(epicId1), "Список не получен");
        assertNotNull(taskManager.getSubtasksByEpicId(epicId2), "Список не получен");

        assertEquals(2, taskManager.getSubtasksByEpicId(epicId1).size(), "Размер списка не верный");
        assertEquals(1, taskManager.getSubtasksByEpicId(epicId2).size(), "Размер списка не верный");
    }

    @Test
    void getHistory() {
        assertEquals(0, taskManager.getHistory().size(), "Неверный размер списка");

        final Task task = new Task("Name", "Description", NEW);
        final int id1 = taskManager.addTask(task);
        final Epic epic = new Epic("Name", "Description");
        taskManager.addEpic(epic);
        final Subtask subtask = new Subtask("Name", "Description", NEW, epic.getId());
        final int id2 = taskManager.addSubtask(subtask);

        assertEquals(0, taskManager.getHistory().size(), "Неверный размер списка");

        taskManager.getTaskById(id1);
        taskManager.getSubtaskById(id2);

        assertEquals(2, taskManager.getHistory().size(), "Неверный размер списка");

        assertTrue(taskManager.getHistory().contains(task));
        assertTrue(taskManager.getHistory().contains(subtask));
        assertFalse(taskManager.getHistory().contains(epic));
    }

    private void testEpicStatusForSubtaskAdd() {
        taskManager = getNewTaskManager();
        Epic epic = new Epic("Name", "Description");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Name", "Description", NEW, epicId);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Name", "Description", NEW, epicId);
        taskManager.addSubtask(subtask2);

        assertEquals(NEW, epic.getStatus(), "Неверный статус эпика");

        subtask1.setStatus(DONE);
        subtask2.setStatus(DONE);
        Subtask subtask3 = new Subtask("Name", "Description", DONE, epicId);
        taskManager.addSubtask(subtask3);

        assertEquals(DONE, epic.getStatus(), "Неверный статус эпика");

        Subtask subtask4 = new Subtask("Name", "Description", NEW, epicId);
        taskManager.addSubtask(subtask4);

        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");

        subtask1.setStatus(IN_PROGRESS);
        subtask2.setStatus(IN_PROGRESS);
        subtask3.setStatus(IN_PROGRESS);
        subtask4.setStatus(IN_PROGRESS);
        Subtask subtask5 = new Subtask("Name", "Description", IN_PROGRESS, epicId);
        taskManager.addSubtask(subtask5);

        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");
    }

    private void testEpicStatusForSubtaskUpdate() {
        int epicId;
        Subtask newSubtask;
        Epic epic;
        taskManager = getNewTaskManager();
        epic = new Epic("Name", "Description");
        epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Name1", "Description1", NEW, epicId);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Name2", "Description2", NEW, epicId);
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Name3", "Description3", NEW, epicId);
        taskManager.addSubtask(subtask3);
        newSubtask = new Subtask("Name", "Description", NEW, epicId);
        newSubtask.setId(subtask3.getId());

        taskManager.updateSubtask(newSubtask);
        assertEquals(NEW, epic.getStatus(), "Неверный статус эпика");

        subtask1.setStatus(DONE);
        subtask2.setStatus(DONE);
        subtask3.setStatus(DONE);
        newSubtask.setStatus(DONE);
        taskManager.updateSubtask(newSubtask);
        assertEquals(DONE, epic.getStatus(), "Неверный статус эпика");

        newSubtask.setStatus(NEW);
        taskManager.updateSubtask(newSubtask);
        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");

        subtask1.setStatus(IN_PROGRESS);
        subtask2.setStatus(IN_PROGRESS);
        subtask3.setStatus(IN_PROGRESS);
        newSubtask.setStatus(IN_PROGRESS);
        taskManager.updateSubtask(newSubtask);
        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");

        newSubtask.setStatus(DONE);
        taskManager.updateSubtask(newSubtask);
        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");
    }

    private  int addAnyTypeTask (T tm, Task task) {
        switch (task.getType()) {
            case TASK: return tm.addTask(task);
            case SUBTASK: return tm.addSubtask((Subtask) task);
            case EPIC: return tm.addEpic((Epic) task);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    private void updateAnyTypeTask(T tm, Task task) {
        switch (task.getType()) {
            case TASK: tm.updateTask(task);
                break;
            case SUBTASK: tm.updateSubtask((Subtask) task);
                break;
            case EPIC: tm.updateEpic((Epic) task);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    private  void removeByIdAnyTypeTask(T tm, int taskId) {
        Task task = getAnyTypeTaskById(taskId, tm);
        if (task == null) {
            throw new NullPointerException("Не удалось получить задачу по id");
        }
        switch (task.getType()) {
            case TASK: tm.removeTaskById(taskId);
                break;
            case SUBTASK: tm.removeSubtaskById(taskId);
                break;
            case EPIC: tm.removeEpicById(taskId);
                break;
            default: //return null;
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    private void testTimesForUpdateTaskAndSubtaskType(T tm, TaskType type) {
        Epic epic = new Epic("Name", "Description");
        Task task1;
        Task task2;
        Task newTask;
        switch (type) {
            case TASK:
                task1 = new Task("Name1", "Description1", NEW);
                task2 = new Task("Name2", "Description2", NEW);
                newTask = new Task("newTaskName", "newTaskDescription", NEW);
                break;
            case SUBTASK:
                addAnyTypeTask(tm, epic);
                task1 = new Subtask("Name1", "Description1", NEW, epic.getId());
                task2 = new Subtask("Name2", "Description2", NEW, epic.getId());
                newTask = new Subtask("newTaskName", "newTaskDescription", NEW, epic.getId());
                break;
            case EPIC:
                throw new RuntimeException("Для типа " + type + " проверка не реализована");
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
        addAnyTypeTask(tm, task1);
        int id2 = addAnyTypeTask(tm, task2);

        task1.setDuration(30);
        task1.setStartTime(LocalDateTime.now());

        newTask.setId(id2);
        newTask.setDuration(10);
        newTask.setStartTime(LocalDateTime.now());
        updateAnyTypeTask(tm,newTask);
        assertNotEquals(task2, newTask, "Задача записалась");

        newTask.setDuration(25);
        newTask.setStartTime(LocalDateTime.now().minusMinutes(20));
        updateAnyTypeTask(tm,newTask);
        assertNotEquals(task2, newTask, "Задача опять записалась");

        newTask.setDuration(50);
        newTask.setStartTime(LocalDateTime.now().minusMinutes(10));
        updateAnyTypeTask(tm,newTask);
        assertNotEquals(task2, newTask, "Задача еще раз записалась");

        newTask.setDuration(10);
        newTask.setStartTime(LocalDateTime.now().minusMinutes(20));
        updateAnyTypeTask(tm,newTask);
        assertEquals(task2, newTask, "Задача не записалась");

        if (type == SUBTASK) { // У эпика расчетное время
            LocalDateTime start = task1.getStartTime().isBefore(task2.getStartTime()) ? task1.getStartTime() :
                    task2.getStartTime();
            LocalDateTime end = task1.getEndTime().isAfter(task2.getEndTime()) ? task1.getEndTime() :
                    task2.getEndTime();
            addAnyTypeTask(tm, new Subtask("Name1", "Description1", NEW, epic.getId()));

            assertEquals(start, epic.getStartTime(), "Время старта у Эпика не совпадает ");
            assertEquals(end, epic.getEndTime(), "Время окончания у Эпика не совпадает ");
            assertEquals(task1.getDuration() + task2.getDuration(), epic.getDuration(),
                    "Продолжительность у Эпика не совпадает ");
        }
    }

}
