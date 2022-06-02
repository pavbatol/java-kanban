import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    private final ArrayList<Integer> subtaskIdList; // Принадлежность подзадач эпику

    public Epic(String name, String description) {
        super(name, description);
        subtaskIdList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public int getSubtaskId(int index) {
        return subtaskIdList.get(index);
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIdList.add(subtaskId);
    }

    public void removeSubtaskId(int index) {
        subtaskIdList.remove(index);
    }

    public void removeSubtaskIdByValue(int subtaskId) {
        for (int i = subtaskIdList.size()-1; i >= 0; i--) {
            if (subtaskIdList.get(i) == subtaskId) {
                subtaskIdList.remove(i);
                break;
            }
        }
    }

    @Override
    public void copySafely(Task task) {
        if (task == null) {
            return;
        }
        setName(task.getName());
        setDescription(task.getDescription());
        // Статус не меняем здесь т.к. это эпик и он синхронизируется по статусу подзадач
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIdList, epic.subtaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIdList);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtaskId=" + subtaskIdList.toString() +
                '}' + "\n";
    }
}
