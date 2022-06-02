import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    private final ArrayList<Integer> subtaskIds; // Принадлежность подзадач эпику

    public Epic(String name, String description) {
        super(name, description);
        subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskById(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskById(int subtaskId) {
        for (int i = subtaskIds.size()-1; i >= 0; i--) {
            if (subtaskIds.get(i) == subtaskId) {
                subtaskIds.remove(i);
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtaskId=" + subtaskIds.toString() +
                '}' + "\n";
    }
}
