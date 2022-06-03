import java.util.Objects;

public class Subtask extends Task{
    private int epicId; // Принадлежность к эпику
    /**
     *  По замечанию №4:
     *  Решил убрать final, чтобы в дальнейшем не создавать себе помех в тестах, добавил setEpicId(int epicId) для него.
     *  Но правильно ли я поступаю, оставляя логику с учетом невозможности существования подзадачи без эпика?
     *  То есть через updateSubtask() я все равно не даю поменять эпик. И при добавлении подзадачи она не добавиться,
     *  если указанного эпика нет. А так же, при удалении эпика, удаляю все подзадачи. Только для теста оставляю,
     *  чтоб через сетер поменять эпик, а не создавать новую подзадачу?
     *  P.S. По всем остальным замечаниям по всему проекту все исправил. Надеюсь ничего не упустил.
     *  Спасибо за подробные разъяснения!
     */

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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
