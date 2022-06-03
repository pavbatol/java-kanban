import java.util.Objects;

public class Subtask extends Task{
    private final int epicId; // Принадлежность к эпику
    /**
     *  По замечанию №4:
     *  Исходил из того, что подзадача не может существовать без эпика.
     *  По тестам вроде надобности не возникло создавать без эпика и не может перемещаться из эпика в эпик.
     *  P.S. по всем остальным замечаниям по все проекту все исправил, спасибо за подробные разъяснения!
     */

    public Subtask(String name, String description, int epicId) {
        super(name, description);
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}' + "\n";
    }
}
