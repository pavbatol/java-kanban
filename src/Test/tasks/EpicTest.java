package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    List<Integer> subtaskIds;

    @BeforeEach
    public void beforeEach() {
        epic = new Epic("name", "description");
    }

    @Test
    void getSubtaskIds() {
        subtaskIds = epic.getSubtaskIds();
        assertEquals(0, subtaskIds.size());
    }

    @Test
    void addSubtaskById() {
        subtaskIds = epic.getSubtaskIds();
        epic.addSubtaskById(0);
        assertEquals(0, epic.getSubtaskIds().get(0));
    }

    @Test
    void removeSubtaskById() {
        subtaskIds = epic.getSubtaskIds();
        epic.addSubtaskById(0);
        epic.removeSubtaskById(0);
        assertEquals(0, subtaskIds.size());
    }

    @Test
    void clearSubtaskIds() {
        subtaskIds = epic.getSubtaskIds();
        epic.addSubtaskById(0);
        epic.addSubtaskById(1);
        epic.clearSubtaskIds();
        assertEquals(0, subtaskIds.size());
    }

    @Test
    void getEndTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2022,8,3,23,0);
        epic.setEndTime(localDateTime);
        assertEquals(localDateTime, epic.getEndTime());
    }

    @Test
    void setEndTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2022,8,3,23,0);
        epic.setEndTime(localDateTime);
        assertEquals(localDateTime, epic.getEndTime());
    }

    @Test
    void testEquals() {
        Epic epic1 = new Epic("name", "description");
        boolean is = epic.equals(epic1);
        assertTrue(is);
    }

    @Test
    void testHashCode() {
        Epic epic1 = new Epic("name", "description");
        boolean is = epic.hashCode() == epic1.hashCode();
        assertTrue(is);
        epic1.setId(0);
        is = epic.hashCode() == epic1.hashCode();
        assertFalse(is);
    }

    @Test
    void testToString() {
        String str = epic.toString();
        boolean is = str.length() > 0;
        assertTrue(is);
    }
}