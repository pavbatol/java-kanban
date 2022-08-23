package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static tasks.TaskStatus.NEW;
import static util.Functions.getTaskType;

public class Epic extends Task {
    private final List<Integer> subtaskIds;
    private LocalDateTime endTime;

//    public Epic(String name, String description) {
//        super(name, description, TaskStatus.NEW);
//        subtaskIds = new ArrayList<>();
//        endTime = null;
//    }
//
//    public Epic(int userId, String name, String description) {
//        super(userId, name, description, TaskStatus.NEW);
//        subtaskIds = new ArrayList<>();
//        endTime = null;
//    }
//
//    public Epic(int userId, String name, String description, TaskStatus status) {
//        super(userId, name, description, status);
//        subtaskIds = new ArrayList<>();
//        endTime = null;
//    }

    public Epic(String name, String description) {
        this(name, description, NEW);
    }

    public Epic(String name, String description, TaskStatus status) {
        this(-1, name, description, status);
    }

    public Epic(int userId, String name, String description, TaskStatus status) {
        this(-1, userId, name, description, status);
    }

    public Epic(int id, int userId, String name, String description, TaskStatus status) {
        super(id, userId, name, description, status);
        subtaskIds = new ArrayList<>();
        endTime = null;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskById(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskById(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtaskIds.equals(epic.subtaskIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, endTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", userId=" + getUserId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", type=" + getType() +
                ", duration=" + getDuration() +
                ", startTime=" + (getStartTime() != null ? getStartTime().toString() : null) +
                ", endTime=" + (getEndTime() != null ? getEndTime().toString() : null) +
                ", subtaskId=" + subtaskIds.toString() +
                '}';
    }

}
