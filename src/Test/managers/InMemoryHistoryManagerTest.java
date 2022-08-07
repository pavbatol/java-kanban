package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void add() {
        final Task task1 = new Task("Name", "Description", NEW);
        final Task task2 = new Task("Name", "Description", NEW);
        final Task task3 = new Task("Name", "Description", NEW);

        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "Неверный размер списка");

        historyManager.add(task2);
        historyManager.add(task3);
        history = historyManager.getHistory();

        assertEquals(1, history.size(), "Добавляются одинаковые задачи"); // у всех id = -1

        task2.setId(1);
        task3.setId(2);
        historyManager.add(task2);
        historyManager.add(task3);
        history = historyManager.getHistory();

        assertEquals(3, history.size(), "Неверный размер списка"); // разные id
    }

    @Test
    void remove() {
        final Task task1 = new Task("Name", "Description", NEW);
        final Task task2 = new Task("Name", "Description", NEW);
        final Task task3 = new Task("Name", "Description", NEW);
        final Task task4 = new Task("Name", "Description", NEW);
        final Task task5 = new Task("Name", "Description", NEW);
        task1.setId(0);
        task2.setId(1);
        task3.setId(2);
        task4.setId(3);
        task5.setId(4);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        List<Task> history = historyManager.getHistory();

        assertEquals(5, history.size(), "Неверный размер списка");

        historyManager.remove(task3.getId()); //из середины
        history = historyManager.getHistory();
        assertEquals(4, history.size(), "Неверный размер списка");
        assertFalse(history.contains(task3), "Задача не удалена");

        historyManager.remove(task1.getId()); //из начала
        history = historyManager.getHistory();
        assertEquals(3, history.size(), "Неверный размер списка");
        assertFalse(history.contains(task1), "Задача не удалена");

        historyManager.remove(task5.getId()); //из конца
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверный размер списка");
        assertFalse(history.contains(task5), "Задача не удалена");
    }

    @Test
    void getHistory() {
        final Task task1 = new Task("Name", "Description", NEW);
        final Task task2 = new Task("Name", "Description", NEW);
        final Task task3 = new Task("Name", "Description", NEW);
        task1.setId(0);
        task2.setId(1);
        task3.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(3, history.size(), "Неверный размер списка");

    }

    @Test
    void testToString() {
        assertTrue(toString().length() > 0);
    }
}