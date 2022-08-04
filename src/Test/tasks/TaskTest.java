package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.*;

class TaskTest {
    Task task;

    @BeforeEach
    void setUp() {
        task = new Task("name", "description", NEW);
    }

    @Test
    void getId() {
        int id = task.getId();
        assertEquals(-1, id);
        task.setId(0);
        id = task.getId();
        assertEquals(0, id);
    }

    @Test
    void setId() {
        getId();
    }

    @Test
    void getName() {
        String name = task.getName();
        assertEquals("name", name);
    }

    @Test
    void setName() {
        task.setName("aaa");
        String name = task.getName();
        assertEquals("aaa", name);
    }

    @Test
    void getDescription() {
        String s = task.getDescription();
        assertEquals("description", s);
    }

    @Test
    void setDescription() {
        task.setDescription("aaa");
        String s = task.getDescription();
        assertEquals("aaa", s);
    }

    @Test
    void getStatus() {
        TaskStatus ts = task.getStatus();
        assertEquals(TaskStatus.NEW, ts);
    }

    @Test
    void setStatus() {
        task.setStatus(TaskStatus.DONE);
        TaskStatus ts = task.getStatus();
        assertEquals(TaskStatus.DONE, ts);
    }

    @Test
    void getType() {
        TaskType type = task.getType();
        assertEquals(TaskType.TASK, type);
        Subtask subtask = new Subtask("name", "description", NEW, 0);
        type = subtask.getType();
        assertEquals(TaskType.SUBTASK, type);
        Epic epic = new Epic("name", "description");
        type = epic.getType();
        assertEquals(TaskType.EPIC, type);
    }

    @Test
    void getDuration() {
        int i = task.getDuration();
        assertEquals(0, i);
    }

    @Test
    void setDuration() {
        task.setDuration(1);
        assertEquals(1,task.getDuration());
    }

    @Test
    void getStartTime() {
        task.setStartTime(null);
        assertNull(task.getStartTime());
        task.setStartTime(LocalDateTime.of(2022,8,4,11,0));
        assertEquals("2022-08-04T11:00", task.getStartTime().toString());
    }

    @Test
    void setStartTime() {
        getStartTime();
    }

    @Test
    void getEndTime() {
        task.setStartTime(LocalDateTime.of(2022,8,4,11,0));
        task.setDuration(20);
        assertEquals("2022-08-04T11:20", task.getEndTime().toString());
    }

    @Test
    void testEquals() {
        Task task2 = new Task("name", "description", NEW);
        boolean is = task.equals(task2);
        assertTrue(is);
        task2.setId(0);
        is = task.equals(task2);
        assertFalse(is);
    }

    @Test
    void testHashCode() {
        Task task2 = new Task("name", "description", NEW);
        boolean is = task.hashCode() == task2.hashCode();
        assertTrue(is);
        task2.setId(0);
        is = task.hashCode() == task2.hashCode();
        assertFalse(is);
    }

    @Test
    void testToString() {
        String str = task.toString();
        boolean is = str.length() > 0;
        assertTrue(is);
    }
}