package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    Subtask subtask;

    @BeforeEach
    public void beforeEach() {
        subtask = new Subtask("name", "description", 1);
    }
    @Test
    void getEpicId() {
        int id = subtask.getEpicId();
        assertEquals(1, id);
    }

    @Test
    void testEquals() {
        Subtask subtask1 = new Subtask("name", "description", 1);
        boolean is = subtask.equals(subtask1);
        assertTrue(is);
    }

    @Test
    void testHashCode() {
        Subtask subtask1 = new Subtask("name", "description", 1);
        boolean is = subtask.hashCode() == subtask1.hashCode();
        assertTrue(is);
        subtask1.setId(0);
        is = subtask.hashCode() == subtask1.hashCode();
        assertFalse(is);
    }

    @Test
    void testToString() {
        String str = subtask.toString();
        boolean is = str.length() > 0;
        assertTrue(is);
    }
}