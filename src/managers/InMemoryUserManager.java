package managers;

import tasks.Task;
import tasks.User;
import util.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryUserManager implements UserManager{
    private int lastId;
    private final Map<Integer, User> users;
    private final TaskManager tm;

    public InMemoryUserManager() {
        this(Managers.getNewInMemoryTaskManager());
    }

    public InMemoryUserManager(TaskManager tm) {
        this.lastId = -1;
        this.users = new HashMap<>();
        this.tm = tm;
    }

    @Override
    public int addUser(User user) {
        if (user == null) {
            return - 1;
        }
        int id = generateId();
        user.setId(id);
        users.put(id, user);
        return id;
    }

    @Override
    public void updateUser(User user) {
        if (user == null) {
            return;
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
    }

    @Override
    public void removeUser(int id) {
        users.remove(id);
    }

    @Override
    public void removeUsers() {
        users.clear();
    }


    @Override
    public User getUser(int id) {
            return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<Task> getUserAllTasks(int userId) {
        List<Task> result = new ArrayList<>(tm.getTasks());
        result.addAll(tm.getEpics());
        result.addAll(tm.getSubtasks());
        return result.stream()
                .filter(task -> task.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public TaskManager getTaskManager() {
        return this.tm;
    }

    int generateId() {
        return ++lastId;
    }

    @Override
    public String toString() {
        final Map<String, String> strs = new HashMap<>(Map.of(
                "users", ""));
        users.forEach((id, user) -> strs.put("users",  strs.get("users") + "\t\t" + user.toString() + "\n"));
        return "InMemoryUserManager{" +
                "\n\tlastId=" + lastId +
                "\n\tusers=\n" + strs.get("users") +
                "\n\ttm=" + tm.toString().replace("\t", "\t\t") +
                '}';
    }
}
