package managers;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    Path path = Paths.get("resourcesTest", "backTest.csv");

    @Override
    protected FileBackedTaskManager getTaskManager() {
        Path path = Paths.get("resourcesTest", "backTest.csv");
        return new FileBackedTaskManager(path);
    }

    @Test
    void loadFromFile() {
        final Task task1 = new Task("Name", "Description", NEW);
        final Task task2 = new Task("Name", "Description", NEW);
        final Epic epic1 = new Epic("Name", "Description");
        final Epic epic2 = new Epic("Name", "Description");
        final int epicId1 = taskManager.addEpic(epic1);
        final int epicId2 = taskManager.addEpic(epic2);
        final Subtask subtask1 = new Subtask("Name", "Description", NEW, epicId1);
        final Subtask subtask2 = new Subtask("Name", "Description", IN_PROGRESS, epicId1);
        final Subtask subtask3 = new Subtask("Name", "Description", DONE, epicId1);

        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                System.out.println("Ошибка удаления");
                throw new RuntimeException("Ошибка удаления");
            }
        }

        FileBackedTaskManager tmSecond = FileBackedTaskManager.loadFromFile(path);
        assertEquals(0, tmSecond.getTasks().size(), "Есть задачи");
        assertEquals(0, tmSecond.getSubtasks().size(), "Есть подзадачи");
        assertEquals(0, tmSecond.getEpics().size(), "Есть эпики");
        assertEquals(0, tmSecond.getHistory().size(), "Есть история");

        final int id1 = taskManager.addTask(task1);
        final int id2 = taskManager.addTask(task2);
        final int id3 = taskManager.addSubtask(subtask1);
        final int id4 = taskManager.addSubtask(subtask2);
        final int id5 = taskManager.addSubtask(subtask3);
        taskManager.getTaskById(id1);
        taskManager.getTaskById(id2);
        taskManager.getSubtaskById(id3);
        taskManager.getSubtaskById(id4);
        taskManager.getSubtaskById(id5);

        tmSecond = FileBackedTaskManager.loadFromFile(path);
        System.out.println("\n-------\n1-ый менеджер:\n" + taskManager);
        System.out.println("\n-------\n2*ой менеджер:\n" + tmSecond);

        assertArrayEquals(taskManager.getTasks().toArray(), tmSecond.getTasks().toArray(),
                "Списки задач не равны");
        assertArrayEquals(taskManager.getSubtasks().toArray(), tmSecond.getSubtasks().toArray(),
                "Списки подзадач не равны");
        assertArrayEquals(taskManager.getEpics().toArray(), tmSecond.getEpics().toArray(),
                "Списки эпиков не равны");
        assertArrayEquals(taskManager.getHistory().toArray(), tmSecond.getHistory().toArray(),
                "Списки истории не равны");


    }

    @Test
    void save() throws ManagerSaveException {
        final Task task1 = new Task("Name", "Description", NEW);
        taskManager.addTask(task1);

//        Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString("rw-w--w--");
//        FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(ownerWritable);

        try {
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("r--rw-rw-"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Разрешение нп запись в файл: " + Files.isWritable(path));

        try {
            ManagerSaveException ex = assertThrows(
                    ManagerSaveException.class,
                    () -> taskManager.save()
            );
            assertEquals("Ошибка записи", ex.getMessage());
        } finally {
            try {
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rw-r--r--"));
            } catch (IOException e) {
                System.out.println("не удалось восстановить права");
            }
        }

        System.out.println("Разрешение нп запись в файл: "  +Files.isWritable(path));
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
