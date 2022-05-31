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

    // Проверяет по ID есть ли задача и Субзадача ли это
    public boolean checkFoSubtask(int id) {
        if (!tasks.containsKey(id)) {
            return  false;
        }
        Object object = tasks.get(id);
        return object.getClass().getName().equals(Subtask.class.getName());
    }

    // Получает имя класса по типу задачи
    public String getClassNameByTaskType(TaskType taskType) {
        if (taskType == null) {
            return null;
        }
        String className = null;
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
    // +++2.1 Получение списка всех задач по типу.
    public ArrayList<Object> getTasksByType(TaskType taskType) {
        ArrayList<Object> resultList = new ArrayList<>();
        String className = getClassNameByTaskType(taskType);
        if (className == null) {
            System.out.println("Список не получен, не удалось получить className");
            return null;
        }
        for (int key : tasks.keySet()) {
            Object object = tasks.get(key);
            if (object == null) {
                continue;
            }
            if (object.getClass().getName().equals(className)) {
                resultList.add(object);
            }
        }
        return resultList;

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

    // +++2.2 Удаление всех задач по типу.
    public void deleteTasksByType(TaskType taskType) {
        ArrayList<Integer> idForDeleteList = new ArrayList<>();
        String className = getClassNameByTaskType(taskType);
        if (className == null) {
            System.out.println("Удаление не выполнено, не удалось получить className");
            return;
        }
        //Собираем нужные ID
        for (int key : tasks.keySet()) {
            Object object = tasks.get(key);
            if (object == null) {
                continue;
            }
            if (object.getClass().getName().equals(className)) {
                idForDeleteList.add(key);
            }
        }
        // Удаляем по собранным ID
        for (Integer id : idForDeleteList) {
            deleteTaskById(id);
        }

    }

    // +++2.3 Получение по идентификатору.
    public Object getTaskById(int id) {
        return tasks.getOrDefault(id, null);
    }

    // 2.4 Создание. Сам объект должен передаваться в качестве параметра.
    // +++Создание для Task
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

    // +++Создание для Subtask
    public void createSubtask(String name, String description, int epicId) {
        if (!checkFoEpic(epicId)) {
            System.out.println("Подзадача НЕ создана, эпик с id = " + epicId + "  не найден");
            return;
        }
        int id = getNewId();
        Epic epic = (Epic) tasks.get(epicId);
        epic.addSubtaskId(id);
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

    // +++Создание для Epic
    public void createEpic(String name, String description) {
        Epic epic = new Epic(getNewId(), name, description);
        tasks.put(id, epic);
    }

    public void createEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Эпик НЕ создан, объект не инициализирован");
            return;
        }
        createEpic(epic.getName(), epic.getDescription());
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

    // 2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateAnyTask(int id, Object object) {
        if (object == null) {
            System.out.println("Задача НЕ обновлена, объект не инициализирован");
            return;
        }
        if (!tasks.containsKey(id)) {
            System.out.println("Задача НЕ обновлена, ID не найден");
            return;
        }
        Task newTask = (Task) object;
        TaskType taskType = getTaskTypeByObject(object);
        switch (taskType) {
            case TASK:
                Task originTask = (Task) tasks.get(id);
                originTask.setName(newTask.getName());
                originTask.setDescription(newTask.getDescription());
                originTask.setStatus(newTask.getStatus());
                tasks.put(id, originTask);
                break;
            case SUBTASK:
                Subtask originSubtask = (Subtask) tasks.get(id);
                originSubtask.setName(newTask.getName());
                originSubtask.setDescription(newTask.getDescription());
                originSubtask.setStatus(newTask.getStatus());
                tasks.put(id, originSubtask);
                break;
            case EPIC:
                Epic originEpic = (Epic) tasks.get(id);
                originEpic.setName(newTask.getName());
                originEpic.setDescription(newTask.getDescription());
                originEpic.setStatus(newTask.getStatus());
                tasks.put(id, originEpic);
                break;
            default:
                System.out.println("Задача НЕ обновлена, такой тип не поддерживается");
        }




//        TaskType taskType = getTaskTypeByObject(object);
//        switch (taskType){
//            case TASK:
//                Task task = (Task) object;
//                Task newTask = new Task(id, task.getName(), task.getDescription());
//                newTask.setStatus(task.getStatus());
//                tasks.put(id, newTask);
//                break;
//            case SUBTASK:
//                Subtask originSubtask = (Subtask) tasks.get(id);
//                Subtask subtask = (Subtask) object;
//                if (subtask.getEpicId() != originSubtask.getEpicId()) {
//                    System.out.println("Задача НЕ обновлена, нельзя менять ID Эпика");
//                    return;
//                }
//                Subtask newSubtask = new Subtask(id, subtask.getName(), subtask.getDescription(), subtask.getEpicId());
//                newSubtask.setStatus(subtask.getStatus());
//                tasks.put(id, newSubtask);
//                break;
//            case EPIC:
//                Epic originEpic = (Epic) tasks.get(id);
//                Epic epic = (Epic) object;
//                originEpic.setName(epic.getName());
//                originEpic.setDescription(epic.getDescription());
//                originEpic.setStatus(epic.getStatus());
////                if (epic.getSubtaskIdList() != originEpic.getSubtaskIdList()) {
////                    System.out.println("Задача НЕ обновлена, нельзя менять ID Эпика");
////                    return;
////                }
////                Epic newEpic = new Epic(id, epic.getName(), epic.getDescription());
////                newEpic.setStatus(epic.getStatus());
////                newEpic.getSubtaskIdList() = originEpic.getSubtaskIdList();
//                tasks.put(id, originEpic);
//                break;
//            default:
//                System.out.println("Задача НЕ обновлена, такой тип не поддерживается");
//        }
    }

    // +++2.6 Удаление по идентификатору.
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого ID = " + id + " нет");
            return;
        }
        // Если Subtask или Epic, то ищем связи и меняем/удаляем их
        if (checkFoSubtask(id)) {
            for (int key : tasks.keySet()) {
                if (!checkFoEpic(key)) {
                    continue;
                }
                Epic epic = (Epic) tasks.get(key);
                if (epic != null && epic.getSubtaskIdList() != null) {
                    epic.removeSubtaskIdByValue(id);
                }
            }
            tasks.remove(id);
        } else if (checkFoEpic(id)) {
            Epic epic = (Epic) tasks.get(id);
            for (int subtaskId : epic.getSubtaskIdList()) {
                tasks.remove(subtaskId);
            }
            tasks.remove(id);
        } else {
            tasks.remove(id);
        }
//        System.out.println("Задача удалена");



    }

    // 3 Дополнительные методы:
    // 3.1 Получение списка всех подзадач определённого эпика.



}
