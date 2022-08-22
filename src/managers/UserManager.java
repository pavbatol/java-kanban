package managers;

import tasks.Task;
import tasks.User;

import java.util.List;

public interface UserManager {
    int addUser(User user);

    void updateUser(User user);

    void removeUser(int id);

    void removeUsers();

    User getUser(int id);

    List<User> getUsers();

    List<Task> getUserAllTasks(int uaerId);

    TaskManager getTaskManager();

}
