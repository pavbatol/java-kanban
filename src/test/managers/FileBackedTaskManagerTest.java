package managers;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    Path path = Paths.get("resourcesTest", "backTest.csv");

    Task task1;
    Task task2;
    Epic epic1;
    Epic epic2;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;

    @Override
    protected FileBackedTaskManager getNewTaskManager() {
        return new FileBackedTaskManager(path);
    }

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();

        task1 = new Task("Name", "Description", NEW);
        task2 = new Task("Name", "Description", NEW);
        epic1 = new Epic("Name", "Description");
        epic2 = new Epic("Name", "Description"); // пустой эпик
        int epicId1 = taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        subtask1 = new Subtask("Name", "Description", NEW, epicId1);
        subtask2 = new Subtask("Name", "Description", IN_PROGRESS, epicId1);
        subtask3 = new Subtask("Name", "Description", DONE, epicId1);
    }

    @Test
    void loadFromFile() {
        //Проверка сохранения/восстановления
//        final Task task1 = new Task("Name", "Description", NEW);
//        final Task task2 = new Task("Name", "Description", NEW);
//        final Epic epic1 = new Epic("Name", "Description");
//        final Epic epic2 = new Epic("Name", "Description"); // пустой эпик
//        final int epicId1 = taskManager.addEpic(epic1);
//        taskManager.addEpic(epic2);
//        final Subtask subtask1 = new Subtask("Name", "Description", NEW, epicId1);
//        final Subtask subtask2 = new Subtask("Name", "Description", IN_PROGRESS, epicId1);
//        final Subtask subtask3 = new Subtask("Name", "Description", DONE, epicId1);

        // Удалим файл если есть
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка удаления");
            }
        }

        // Проверка на пустом файле
        FileBackedTaskManager tmSecond = FileBackedTaskManager.loadFromFile(path);
        assertEquals(0, tmSecond.getTasks().size(), "Есть задачи");
        assertEquals(0, tmSecond.getSubtasks().size(), "Есть подзадачи");
        assertEquals(0, tmSecond.getEpics().size(), "Есть эпики");
        assertEquals(0, tmSecond.getHistory().size(), "Есть история");

        // Проверка с задачами, но без истории
        final int id1 = taskManager.addTask(task1);
        int timeStep = taskManager.getTimesManager().getTimeStep();
        task1.setDuration(timeStep * 5);
        task1.setStartTime(LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0,
                0));
        taskManager.updateTask(task1);

        final int id2 = taskManager.addTask(task2);
        final int id3 = taskManager.addSubtask(subtask1);
        final int id4 = taskManager.addSubtask(subtask2);
        final int id5 = taskManager.addSubtask(subtask3);

        tmSecond = FileBackedTaskManager.loadFromFile(path);
        //System.out.println("\n-------\n1-ый менеджер:\n" + taskManager);
        //System.out.println("\n-------\n2*ой менеджер:\n" + tmSecond);

        assertArrayEquals(taskManager.getTasks().toArray(), tmSecond.getTasks().toArray(),
                "Списки задач не равны");
        assertArrayEquals(taskManager.getSubtasks().toArray(), tmSecond.getSubtasks().toArray(),
                "Списки подзадач не равны");
        assertArrayEquals(taskManager.getEpics().toArray(), tmSecond.getEpics().toArray(),
                "Списки эпиков не равны");
        assertArrayEquals(taskManager.getHistory().toArray(), tmSecond.getHistory().toArray(),
                "Списки истории не равны");
        assertEquals(0, tmSecond.getHistory().size(), "Есть история");

        // Проверка с задачами и историей
        taskManager.getTaskById(id1);
        taskManager.getTaskById(id2);
        taskManager.getSubtaskById(id3);
        taskManager.getSubtaskById(id4);
        taskManager.getSubtaskById(id5);

        tmSecond = FileBackedTaskManager.loadFromFile(path);
        //System.out.println("\n-------\n1-ый менеджер:\n" + taskManager);
        //System.out.println("\n-------\n2*ой менеджер:\n" + tmSecond);

        assertArrayEquals(taskManager.getTasks().toArray(), tmSecond.getTasks().toArray(),
                "Списки задач не равны");
        assertArrayEquals(taskManager.getSubtasks().toArray(), tmSecond.getSubtasks().toArray(),
                "Списки подзадач не равны");
        assertArrayEquals(taskManager.getEpics().toArray(), tmSecond.getEpics().toArray(),
                "Списки эпиков не равны");
        assertArrayEquals(taskManager.getHistory().toArray(), tmSecond.getHistory().toArray(),
                "Списки истории не равны");
        assertEquals(5, tmSecond.getHistory().size(), "Неверный размер списка истории");
    }

    @Test
    void loadFromFile_shouldBeLoadedTimesInTimeManager() {
        final int id1 = taskManager.addTask(task1);
        final int timeStep = taskManager.getTimesManager().getTimeStep();
        task1.setDuration(timeStep * 2);
        task1.setStartTime(LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0,
                0));
        taskManager.updateTask(task1);

        final int id2 = taskManager.addTask(task2);
        final int id3 = taskManager.addSubtask(subtask1);
        final int id4 = taskManager.addSubtask(subtask2);
        final int id5 = taskManager.addSubtask(subtask3);
        task2.setStartTime(task1.getStartTime().plusMinutes(task1.getDuration()));
        subtask1.setStartTime(task2.getStartTime().plusMinutes(task2.getDuration()));
        subtask2.setStartTime(subtask1.getStartTime().plusMinutes(subtask1.getDuration()));
        subtask3.setStartTime(subtask2.getStartTime().plusMinutes(subtask2.getDuration()));

        FileBackedTaskManager tmSecond = FileBackedTaskManager.loadFromFile(path); // загружаемся из файла

        assertFalse(tmSecond.getTimesManager().isFree(task1.getStartTime(), task1.getEndTime()));
        assertFalse(tmSecond.getTimesManager().isFree(task2.getStartTime(), task2.getEndTime()));
        assertFalse(tmSecond.getTimesManager().isFree(subtask1.getStartTime(), subtask1.getEndTime()));
        assertFalse(tmSecond.getTimesManager().isFree(subtask2.getStartTime(), subtask2.getEndTime()));
        assertFalse(tmSecond.getTimesManager().isFree(subtask3.getStartTime(), subtask3.getEndTime()));
    }

    @Test
    void save() throws ManagerSaveException {
        // Проверка на пустом списке
        ManagerSaveException ex = assertThrows(
                ManagerSaveException.class,
                () -> taskManager.save()
        );
        assertEquals("Нечего сохранять", ex.getMessage());

        // Проверка на НЕ пустом списке - Эпик без подзадач и пустой историей
        final Epic epic1 = new Epic("Name", "Description");
        taskManager.addEpic(epic1);

        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rw-rw-rw-");
        try {
            permissions = Files.getPosixFilePermissions(path);
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("r--rw-rw-"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        assertFalse(Files.isWritable(path)); // Проверим что запретили запись

        try {
            ex = assertThrows(
                    ManagerSaveException.class,
                    () -> taskManager.save() // Проверка на выброс исключения
            );
            assertEquals("Ошибка записи", ex.getMessage());
        } finally {
            try {
                Files.setPosixFilePermissions(path, permissions); // Восстанавливаем прав
            } catch (IOException e) {
                System.out.println("Не удалось восстановить права");
            }
        }
    }

    @Override
    @Test
    void addTask() {
        super.addTask();
    }

    @Override
    @Test
    void addSubtask() {
        super.addSubtask();
    }

    @Override
    @Test
    void addEpic() {
        super.addEpic();
    }

    @Override
    @Test
    void updateTask() {
        super.updateTask();
    }

    @Override
    @Test
    void updateSubtask() {
        super.updateSubtask();
    }

    @Override
    @Test
    void updateEpic() {
        super.updateEpic();
    }

    @Override
    @Test
    void removeTaskById() {
        super.removeTaskById();
    }

    @Override
    @Test
    void removeSubtaskById() {
        super.removeSubtaskById();
    }

    @Override
    @Test
    void removeEpicById() {
        super.removeEpicById();
    }

    @Override
    @Test
    void removeTasks() {
        super.removeTasks();
    }

    @Override
    @Test
    void removeSubtasks() {
        super.removeSubtasks();
    }

    @Override
    @Test
    void removeEpics() {
        super.removeEpics();
    }

    @Override
    @Test
    void getTaskById() {
        super.getTaskById();
    }

    @Override
    @Test
    void getSubtaskById() {
        super.getSubtaskById();
    }

    @Override
    @Test
    void getEpicById() {
        super.getEpicById();
    }

    @Test
    void testToString() {
        assertTrue(taskManager.toString().length() > 0);
    }
}
