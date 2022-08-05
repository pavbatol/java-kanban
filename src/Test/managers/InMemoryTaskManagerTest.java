package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.*;
import static tasks.TaskType.*;
import static util.Functions.getAnyTypeTaskById;

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
        testTimesForUpdateTaskAndSubtaskType(new InMemoryTaskManager(), TASK);
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
        testTimesForUpdateTaskAndSubtaskType(new InMemoryTaskManager(), SUBTASK);
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Name", "Description");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Name1", "Description1", NEW, epicId);
        int id1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Name2", "Description2", DONE, epicId);
        int id2 = taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Name3", "Description3", IN_PROGRESS, epicId);
        int id3 = taskManager.addSubtask(subtask3);
        Epic newEpic = new Epic("newName", "newDescription");

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

    private void testEpicStatusForSubtaskAdd() {
        taskManager = new InMemoryTaskManager();
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
        taskManager = new InMemoryTaskManager();
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

    private <T extends Task>  Executable generateExecutable(TaskManager tm, T task) {
        switch (task.getType()) {
            case TASK: return () -> tm.updateTask(task);
            case SUBTASK: return () -> tm.updateSubtask((Subtask) task);
            case EPIC: return () -> tm.updateEpic((Epic) task);
            default: //return null;
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }

        //        ValidateException ex = assertThrows(
//                ValidateCrossingTimeException.class,
//                generateExecutable(newTask)
//        );
//        Assertions.assertEquals("!!! Недопустимо пересечение по времени.", ex.getMessage());

//        ex = assertThrows(
//                ValidateCrossingTimeException.class,
//                generateExecutable(newTask)
//        );
//        Assertions.assertEquals("!!! duration должен быть больше 0.", ex.getMessage());
    }

    private <T extends Task> int addAnyTypeTask (TaskManager tm, T task) {
        switch (task.getType()) {
            case TASK: return tm.addTask(task);
            case SUBTASK: return tm.addSubtask((Subtask) task);
            case EPIC: return tm.addEpic((Epic) task);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    private <T extends Task> void updateAnyTypeTask(TaskManager tm, T task) {
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


    private  void removeByIdAnyTypeTask(TaskManager tm, int taskId) {
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

    private void testTimesForUpdateTaskAndSubtaskType(TaskManager tm, TaskType type) {
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
        int id1 = addAnyTypeTask(tm, task1);
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