package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> lastViewedTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        lastViewedTasks.add(task);
        if (lastViewedTasks.size() > 10) {
            lastViewedTasks.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return lastViewedTasks;
    }
}
