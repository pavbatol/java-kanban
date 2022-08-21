package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.User;
import util.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryUserManager implements UserManager, TaskManager{
    private int lastId;
    private final Map<Integer, User> users;
    private final TaskManager tm;

    public InMemoryUserManager() {
        this.lastId = -1;
        this.users = new HashMap<>();
        this.tm = Managers.getNewInMemoryTaskManager();
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
    public User getUser(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
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
    public int addTask(Task task) {
        return 0;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        return 0;
    }

    @Override
    public int addEpic(Epic epic) {
        return 0;
    }

    @Override
    public void updateTask(Task task) {

    }

    @Override
    public void updateSubtask(Subtask subtask) {

    }

    @Override
    public void updateEpic(Epic epic) {

    }

    @Override
    public void removeTaskById(int id) {

    }

    @Override
    public void removeSubtaskById(int id) {

    }

    @Override
    public void removeEpicById(int id) {

    }

    @Override
    public void removeTasks() {

    }

    @Override
    public void removeSubtasks() {

    }

    @Override
    public void removeEpics() {

    }

    @Override
    public Task getTaskById(int id) {
        return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        return null;
    }

    @Override
    public List<Task> getTasks() {
        return null;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return null;
    }

    @Override
    public List<Epic> getEpics() {
        return null;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return null;
    }
}
