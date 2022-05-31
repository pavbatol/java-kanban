import java.util.ArrayList;
import java.util.HashMap;

enum TaskType {
    TASK,
    SUBTASK,
    EPIC;
}
public class Manager {
    private int id;
    public HashMap<Integer, Object> tasks;
//    public HashMap<Integer, Task> tasks;
//    public HashMap<Integer, Subtask> subtasks;
//    public HashMap<Integer, Epic> epics;

//    public ArrayList<HashMap<Integer, Object>> taskList;

    public Manager() {
        id = -1;
//        taskList = new ArrayList<>();
        tasks = new HashMap<>();
//        subtasks = new HashMap<>();
//        epics = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    private void setId() {
        id++;
    }

    // Установка и получение нового ID
    private int getNewId() {
        setId();
        return getId();
    }

    // Проверяет по ID есть ли задача и Эпик ли это
    public boolean checkFoEpic(int id) {
        if (!tasks.containsKey(id)) {
            return  false;
        }
        Object object = tasks.get(id);
        return object.getClass().getName().equals(Epic.class.getName());
    }
    //  Получение имени класса по типу
    public String getClassNameByType(TaskType taskType) {
        if (taskType == null) {
            return "";
        }
        String className = "";
        switch (taskType){
            case TASK:
                className = Task.class.getName();
                break;
            case SUBTASK:
                className = Subtask.class.getName();
                break;
            case EPIC:
                className = Epic.class.getName();
                break;
            default:
                System.out.println("Такой тип не предусмотрен");
        }
        return className;
    }

    // Получение типа задачи по объекту
    public TaskType getTaskTypeByObject(Object object) {
        if (object.getClass() == Task.class) {
            return TaskType.TASK;
        } else if (object.getClass() == Subtask.class) {
            return TaskType.SUBTASK;
        } else if (object.getClass() == Epic.class) {
            return TaskType.EPIC;
        } else {
            return null;
        }
    }

    // 2 Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // 2.1 Получение списка всех задач по типу.
    public HashMap<Integer, Object> getTaskByType(TaskType taskType) {
        HashMap<Integer, Object> result = new HashMap<>();
        String className = getClassNameByType(taskType);
        for (int key : tasks.keySet()) {
            Object object = tasks.get(key);
            if (object == null) {
                continue;
            }
            if (object.getClass().getName().equals(className)) {
                result.put(key, object);
            }
        }
        return result;

        /*
        public HashMap<Integer, Object> getTaskByType(String className) {
            HashMap<Integer, Object> result = new HashMap<>();
            for (int key : tasks.keySet() ) {
                Object object = tasks.get(key);
                if (object == null) {
                    continue;
                }
                if (object.getClass().getName().equals(className)) {
                    result.put(key, object);
                }
            }
            return result;
        }
        */
    }

    // 2.2 Удаление всех задач по типу.
    public void deleteTasksByType(TaskType taskType) {
        ArrayList<Integer> idForDelete = new ArrayList<>();
        String className = getClassNameByType(taskType);
        //Собираем нужные ID
        for (int key : tasks.keySet()) {
            Object object = tasks.get(key);
            if (object == null) {
                continue;
            }
            if (object.getClass().getName().equals(className)) {
                idForDelete.add(key);
            }
        }
        // Удаляем по собранным ID
        for (Integer id : idForDelete) {
            tasks.remove(id);
        }

    }

    // 2.3 Получение по идентификатору.
    public Object getTaskById(int id) {
        return tasks.getOrDefault(id, null);
    }

    // 2.4 Создание. Сам объект должен передаваться в качестве параметра.
    // -- для Task
    public void createTask(String name, String description) {
        Task task = new Task(getNewId(), name, description);
        tasks.put(id, task);
    }

    public void createTask(Task task) {
        if (task == null) {
            System.out.println("Задача НЕ создана, объект не инициализирован");
            return;
        }
        createTask(task.getName(), task.getDescription());
    }

    // -- для Subtask
    public void createSubtask(String name, String description, int epicId) {
        if (!checkFoEpic(epicId)) {
            System.out.println("Подзадача НЕ создана, эпик с id = " + epicId + "  не найден");
            return;
        }
        int id = getNewId();
        Epic epic = (Epic) tasks.get(epicId);
        epic.subtaskId.add(id);
        Subtask subtask = new Subtask(id, name, description, epicId);
        tasks.put(id, subtask);
    }

    public void createSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Подзадача НЕ создана, объект не инициализирован");
            return;
        }
        createSubtask(subtask.getName(), subtask.getDescription(), subtask.getEpicId());
    }

    // -- для Epic
    public void createEpic(String name, String description) {
        Epic epic = new Epic(getNewId(), name, description);
        tasks.put(id, epic);
    }

    public void createEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Задача НЕ создана, объект не инициализирован");
            return;
        }
        createTask(epic.getName(), epic.getDescription());
    }
/*
    public void createTask(String name, String description, TaskType taskType) {
        int id = getNewId();
        switch (taskType){
            case TASK: {
                Task task = new Task(id, name, description);
                tasks.put(id, task);
                break;
            }
            case SUBTASK: {
                Subtask task = new Subtask(id, name, description, 12312313);
                tasks.put(id, task);
                break;
            }
            case EPIC: {
                Epic task = new Epic(id, name, description);
                tasks.put(id, task);
                break;
            }
            default:
                System.out.println("Тип не найден, задача не создана");
                return;
        }
    }
*/

/*    public void createTask(Object object) {
        TaskType taskType = getTaskTypeByObject(object);
        if (taskType == null) {
            System.out.println("Тип задачи не определен");
            return;
        }
        Task task = (Task) object;
        createTask(task.getName(), task.getDescription(), taskType);
    }*/

    // 2.4 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(int id, Object task) {
        if (task == null) {
            System.out.println("Задача НЕ обновлена, объект не инициализирован");
            return;
        }
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else {
            System.out.println("Такого ID нет");
        }
    }

    // 2.5 Удаление по идентификатору.
    public void deleteTasksById(int id ) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Задача удалена");
        } else {
            System.out.println("Задача НЕ удалена, такого ID нет");
        }
    }

    // 3 Дополнительные методы:
    // 3.1 Получение списка всех подзадач определённого эпика.



}
