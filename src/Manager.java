import java.util.ArrayList;
import java.util.HashMap;

enum TaskType {
    TASK,
    SUBTASK,
    EPIC;
}
public class Manager {
    private static int id;
    public HashMap<Integer, Object> tasks;

    public Manager() {
        id = -1;
        tasks = new HashMap<>();
    }

    private static int getId() {
        return id;
    }

    private static void setId() {
        id = getId() + 1;
    }

    // Установка и получение нового ID
    public static int getNewId() {
        setId();
        return getId();
    }

    // Проверяет в коллекции по ID Эпик ли это
    public boolean isEpic(int id) {
        if (!tasks.containsKey(id)) {
            return  false;
        }
        Object object = tasks.get(id);
        return object.getClass().getName().equals(Epic.class.getName());
    }

    // Проверяет в коллекции по ID Субзадача ли это
    public boolean isSubtask(int id) {
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

    // Получение типа задачи по id в коллекции
    private TaskType getTaskTypeById(int id) {
        if (!tasks.containsKey(id)) {
            return null;
        }
        return getTaskTypeByObject(tasks.get(id));
    }

    // Синхронизация статуса Эпика (если у субзадачи поменяли статус, надо отправить сюда)
    public void synchronizeEpicTaskStatus(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Статусы НЕ синхронизированы, объект не инициализирован");
            return;
        }
        // Проверяем что Субзадача уже есть в коллекции
        int id = subtask.getId();
        if (!tasks.containsKey(id) || tasks.get(id) == null) {
            System.out.println("Статусы НЕ синхронизированы, субзадачи нет в коллекции");
            return;
        }
        // Получаем Эпик и отправляем в перегруженный метод
        int epicId = subtask.getEpicId();
        Epic epic = (Epic) tasks.get(epicId);
        synchronizeEpicTaskStatus(epic);
    }

    public void synchronizeEpicTaskStatus(Epic epic) {
        if (epic == null) {
            System.out.println("Статусы НЕ синхронизированы, объект не инициализирован");
            return;
        }
        // Если Эпика нет в коллекции - выходим
        int epicId = epic.getId();
        if (!tasks.containsKey(epicId)) {
            System.out.println("Статусы НЕ синхронизированы, Эпик не найден");
            return;
        }
        // Проверяем статусы у всех подзадач Эпика
        ArrayList<Integer> subtaskIdList = epic.getSubtaskIdList();
        int doneStatusCount = 0;
        int newStatusCount = 0;

        loop:
        for (int subtaskId : subtaskIdList) {
            if (!tasks.containsKey(subtaskId) || tasks.get(subtaskId) == null) {
                continue;
            }
            // Получаем статус каждой субзадачи
            TaskStatus subtaskStatus = ((Subtask) tasks.get(subtaskId)).status;
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
        if (newStatusCount == subtaskIdList.size()) {   // даже если список был пустой сработает первым условием
            forChangeStatus = TaskStatus.NEW;
        } else if (doneStatusCount == subtaskIdList.size()) {
            forChangeStatus = TaskStatus.DONE;
        }
        // Записываем новый статус в эпик
        if (forChangeStatus != epic.getStatus()) {
            epic.setStatus(forChangeStatus);
//            tasks.put(epicId, epic); // это наверное лишнее
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
    public void createTask(Task task) {
        if (task == null) {
            System.out.println("Задача НЕ создана, объект не инициализирован");
            return;
        }
        tasks.put(task.getId(), task);
    }

    // +++Создание для Subtask
    public void createSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Подзадача НЕ создана, объект не инициализирован");
            return;
        }
        int epicId = subtask.getEpicId();
        if (!isEpic(epicId)) {
            System.out.println("Подзадача НЕ создана, эпик с id = " + epicId + "  не найден");
            return;
        }
        tasks.put(subtask.getId(), subtask);
        // Записываем в список эпика id подзадачи
        Epic epic = (Epic) tasks.get(epicId);
        epic.addSubtaskId(id);
        // Синхронизируем статус в эпике
        synchronizeEpicTaskStatus(subtask);
    }

    // +++Создание для Epic
    public void createEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Эпик НЕ создан, объект не инициализирован");
            return;
        }
        tasks.put(epic.getId(), epic);
    }

    // +++2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateAnyTask(int id, Object object) {
        if (object == null) {
            System.out.println("Задача НЕ обновлена, объект не инициализирован");
            return;
        }
        if (!tasks.containsKey(id)) {
            System.out.println("Задача НЕ обновлена, ID не найден");
            return;
        }

        TaskType taskType = getTaskTypeByObject(object);
        if (taskType == null) {
            System.out.println("Задача НЕ обновлена, на входе не верный тип объекта");
            return;
        }

        Task newTask = (Task) object;
        // Проверим что тип задачи по id соответствует типу присланного объекта
        // getTaskTypeById(id) здесь можно не проверять на null, т.к. taskType уже проверен
        if (taskType != getTaskTypeById(id)) {
            System.out.println("Задача НЕ обновлена, типы объектов не совпадают."
                    + "Тип с id = " + id + " = " + getTaskTypeById(id) + ", тип object = " + taskType);
            return;
        }

        // В зависимости от типа задачи
        switch (taskType) {
            case TASK:
                Task originTask = (Task) tasks.get(id);
                originTask.copySafely(newTask);
                break;
            case SUBTASK:
                Subtask originSubtask = (Subtask) tasks.get(id);
                originSubtask.copySafely(newTask);
                synchronizeEpicTaskStatus(originSubtask); // синхронизируем статус в эпике
                break;
            case EPIC:
                Epic originEpic = (Epic) tasks.get(id);
                originEpic.copySafely(newTask); // в Epic метод переопределен
                break;
            default:
                System.out.println("Задача НЕ обновлена, такой тип не поддерживается");
        }
    }

    // +++2.6 Удаление по идентификатору.
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Удаление не выполнено, такого ID = " + id + " нет");
            return;
        }
        // Если Subtask или Epic, то ищем связи и меняем/удаляем их
        if (isSubtask(id)) {
            // Подготовим лист для сбора Эпиков в которых произойдут изменения
            ArrayList<Epic> epicList = new ArrayList<>();
            for (int key : tasks.keySet()) {
                if (!isEpic(key)) {
                    continue;
                }
                Epic epic = (Epic) tasks.get(key);
                if (epic != null && epic.getSubtaskIdList() != null) {
                    epic.removeSubtaskIdByValue(id); // удаляем id подзадачи из списка в эпике
                    epicList.add(epic); //запоминаем Эпики
                }
            }
            tasks.remove(id); // удаляем и потом переберем измененные Эпики и синхронизируем статус по подзадачам
            for (Epic epic : epicList) {
                synchronizeEpicTaskStatus(epic);
            }
        } else if (isEpic(id)) {
            Epic epic = (Epic) tasks.get(id);
            for (int subtaskId : epic.getSubtaskIdList()) {
                tasks.remove(subtaskId); // вместе с эпиком удаляем все его подзадачи
            }
            tasks.remove(id);
        } else {
            tasks.remove(id); // обычную задачу просто удаляем
        }
    }

    // 3 Дополнительные методы:
    // +++3.1 Получение списка всех подзадач определённого эпика.
    public ArrayList<Object> getTasksByEpic(int epicId) {
        ArrayList<Object> resultList = new ArrayList<>();
        // Проверим что это Эпик и он в списке
        if (!isEpic(epicId)) {
            System.out.println("Эпик с таким id не найден");
            return null;
        }
        // Получаем список подзадач эпика
        Epic epic = (Epic) tasks.get(epicId);
        ArrayList<Integer> subtaskIdList = epic.getSubtaskIdList();
        for (int subtaskId : subtaskIdList) {
            if (!isSubtask(subtaskId)) {
                continue;
            }
            // Собираем в ArrayList для выдачи
            resultList.add(tasks.get(subtaskId));
        }
        return resultList;
    }


}
