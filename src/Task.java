import java.util.Objects;

public class Task {
    private final int id;
    public String name;
    public String description;
    public TaskStatus status;

    public Task(String name, String description) {
        id = Manager.getNewId();
        this.name = name;
        this.description = description;
        status = TaskStatus.NEW;
    }

    public int getId() {
        return id;
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

    // Копирует в свои поля только обобщенные данные из объекта
    public void copySafely(Task task) {
        if (task == null) {
            return;
        }
        setName(task.getName());
        setDescription(task.getDescription());
        setStatus(task.getStatus());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && name.equals(task.name) && description.equals(task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}' + "\n";
    }
}
