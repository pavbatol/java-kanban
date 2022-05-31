import java.util.ArrayList;

public class Epic extends Task{
    public ArrayList<Integer> subtaskId;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subtaskId = new ArrayList<>();
    }


}
