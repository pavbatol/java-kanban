import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtaskIdList;

    public Epic(String name, String description) {
        super(name, description);
        subtaskIdList = new ArrayList<>();
    }

     public Epic(int id, String name, String description) {
        super(id, name, description);
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
            }
        }
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
