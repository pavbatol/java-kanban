import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int id;
    public HashMap<Integer, Task> tasks;
    public HashMap<Integer, Subtask> subtasks;
    public HashMap<Integer, Epic> epics;

    public Manager() {
        id = -1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();

    }

    // Синхронизировать статус у Эпика
    public void synchronizeEpicStatus(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Статусы НЕ синхронизированы, id не найден");
            return;
        }
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Статусы НЕ синхронизированы, объект не инициализирован");
            return;
        }
        // Проверяем статусы у всех подзадач Эпика
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        int doneStatusCount = 0; // считать DON
        int newStatusCount = 0; // считать NEW

        loop:
        for (int subtaskId : subtaskIds) {
            if (!subtasks.containsKey(subtaskId) || subtasks.get(subtaskId) == null) {
                continue;
            }
            // Получаем статус каждой подзадачи
            TaskStatus subtaskStatus =  subtasks.get(subtaskId).getStatus(); // TODO: 02.06.2022 null ???
            switch (subtaskStatus) {
                case IN_PROGRESS:
                    break loop; // можно прервать цикл, уже все ясно
                case DONE:
                    doneStatusCount++;
                    break;
                case NEW:
                    newStatusCount++;
                    break;
            }
        }
        // Определяем новый статус
        TaskStatus forChangeStatus = TaskStatus.IN_PROGRESS;
        if (newStatusCount == subtaskIds.size()) {   // даже если список был пустой сработает это первое условием
            forChangeStatus = TaskStatus.NEW;
        } else if (doneStatusCount == subtaskIds.size()) {
            forChangeStatus = TaskStatus.DONE;
        }
        // Записываем новый статус в эпик
        if (forChangeStatus != epic.getStatus()) {
            epic.setStatus(forChangeStatus);
        }
    }

    /**
     * +++2.1 Получение списка всех задач
    */

    // +++Получение для Task
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    // +++Получение для Subtask
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // +++Получение для Epic
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * ---2.2 Удаление всех задач.
    */

    // +++Удаление для Task
    public void removeTasks() {
        tasks.clear();
    }

    // +++Удаление для Subtask
    public void removeSubtasks() {
        subtasks.clear();
        // Необходимо поменять в эпиках статус после удаления всех подзадач
        for (Epic epic : epics.values()) {
            if (epic != null) {
                epic.setStatus(TaskStatus.NEW);
            }
        }
    }

    // +++Удаление для Epic
    public void removeEpics() {
        epics.clear();
        // Необходимо удалить все подзадачи, т.к. эпиков больше нет
        subtasks.clear();
    }

    /**
    * ---2.3 Получение по идентификатору.
    */

    // +++Получение для Task
    public Task getTaskById(int id) {
        return tasks.getOrDefault(id, null);
    }

    // +++Получение для Subtask
    public Subtask getSubtaskById(int id) {
        return subtasks.getOrDefault(id, null);
    }

    // +++Получение для Epic
    public Epic getEpicById(int id) {
        return epics.getOrDefault(id, null);
    }

    /**
    * ---2.4 Создание. Сам объект должен передаваться в качестве параметра.
    */

    // +++Создание для Task
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("Задача НЕ создана, объект не инициализирован");
            return;
        }
        task.setId(++id);
        tasks.put(task.getId(), task);
    }

    // +++Создание для Subtask
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Подзадача НЕ создана, объект не инициализирован");
            return;
        }
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            System.out.println("Подзадача НЕ создана, эпик с id = " + epicId + "  не найден");
            return;
        }
        subtask.setId(++id);
        subtasks.put(subtask.getId(), subtask);
        // Записываем в список эпика id подзадачи
        Epic epic = epics.get(epicId);
        epic.addSubtaskById(subtask.getId());
        // Синхронизируем статус в эпике
        synchronizeEpicStatus(epicId);
    }

    // +++Создание для Epic
    public void addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Эпик НЕ создан, объект не инициализирован");
            return;
        }
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }

    /**
    * ---2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    */

    // +++Обновление для Task
    public void updateTask(int id, Task task) {
        if (task == null) {
            System.out.println("Задача Task НЕ обновлена, объект не инициализирован");
            return;
        }
        if (!tasks.containsKey(id)) {
            System.out.println("Задача Task НЕ обновлена, id не найден");
            return;
        }
        Task originTask = tasks.get(id);
        originTask.setName(task.getName());
        originTask.setDescription(task.getDescription());
        originTask.setStatus(task.getStatus());
    }

    // +++Обновление для Subtask
    public void updateSubtask(int id, Subtask subtask) {
        if (subtask == null) {
            System.out.println("Задача Subtask НЕ обновлена, объект не инициализирован");
            return;
        }
        if (!subtasks.containsKey(id)) {
            System.out.println("Задача Subtask НЕ обновлена, id не найден");
            return;
        }
        Subtask originSubtask = subtasks.get(id);
        originSubtask.setName(subtask.getName());
        originSubtask.setDescription(subtask.getDescription());
        originSubtask.setStatus(subtask.getStatus());
        // синхронизируем статус в эпике
        int epicId = originSubtask.getEpicId();
        synchronizeEpicStatus(epicId);
    }

    // +++Обновление для Epic
    public void updateEpic(int id, Epic epic) {
        if (epic == null) {
            System.out.println("Задача Epic НЕ обновлена, объект не инициализирован");
            return;
        }
        if (!epics.containsKey(id)) {
            System.out.println("Задача Epic НЕ обновлена, id не найден");
            return;
        }
        Epic originEpic = epics.get(id);
        originEpic.setName(epic.getName());
        originEpic.setDescription(epic.getDescription());
    }

    /**
    * ---2.6 Удаление по идентификатору.
    */

    // +++Удаление для Task
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        tasks.remove(id);
    }

    // +++Удаление для Subtask
    public void removeSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        // Удаляем подзадачу
        subtasks.remove(id);
        // Смотрим Эпик к которому он принадлежал
        if (!epics.containsKey(epicId)) {
            return;
        }
        Epic epic = epics.get(epicId);
        epic.removeSubtaskById(id); // У Эпика удаляем подзадачу
        synchronizeEpicStatus(epicId); // У Эпика синхронизируем статус по подзадачам
    }

    // +++Удаление для Epic
    public void removeEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого id = " + id + " нет");
            return;
        }
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId); // вместе с эпиком удаляем все его подзадачи
        }
        epics.remove(id);
    }

    /**
    *---3.1 Получение списка всех подзадач определённого эпика.
    */

    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик с таким id не найден");
            return null;
        }
        // Получаем список подзадач эпика
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            if (!subtasks.containsKey(subtaskId)) {
                continue;
            }
            // Собираем в ArrayList для выдачи
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

}
