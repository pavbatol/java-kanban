package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

import static tasks.TaskStatus.NEW;
import static util.Functions.getTaskType;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private final TaskType type;
    private long duration;
    private LocalDateTime startTime;
    private final int userId;

    public Task(int id, int userId, String name, String description, TaskStatus status) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.status = status;
        type = getTaskType(getClass());
        duration = 0;
        startTime = null;
    }

    public Task(int userId, String name, String description, TaskStatus status) {
        this(-1, userId, name, description, status);
    }

    public Task(String name, String description, TaskStatus status) {
        this(-1, name, description, status);
    }

    public Task(String name, String description) {
        this(-1, name, description, NEW);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return  getStartTime() != null ? getStartTime().plusMinutes(getDuration()) : null;
    }

    public int getUserId() {
        return this.userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && name.equals(task.name)
                && description.equals(task.description)
                && status == task.status
                && type == task.type
                && duration == task.duration
                && Objects.equals(startTime,task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name,
                description,
                status,
                type,
                duration,
                startTime
        );
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", duration=" + duration +
                ", startTime=" + (startTime != null ? startTime.toString() : null) +
                '}';
    }
}
