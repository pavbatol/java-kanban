package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

import static tasks.TaskStatus.NEW;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        this(name, description, NEW, epicId);
    }

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        this(-1, name, description, status, epicId);
    }

    public Subtask(int userId, String name, String description, TaskStatus status, int epicId) {
        this(-1, userId, name, description, status, epicId);
    }

    public Subtask(int id, int userId, String name, String description, TaskStatus status, int epicId) {
        this(id, userId, name, description, status, 0, null, epicId);
    }

    public Subtask(int id, int userId, String name, String description, TaskStatus status,
                   long duration, LocalDateTime startTime, int epicId) {
        super(id, userId, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", userId=" + getUserId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", type=" + getType() +
                ", duration=" + getDuration() +
                ", startTime=" + (getStartTime() != null ? getStartTime().toString() : null) +
                ", epicId=" + epicId +
                '}';
    }
}
