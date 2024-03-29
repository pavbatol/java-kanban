package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.*;
import static tasks.TaskType.SUBTASK;
import static tasks.TaskType.TASK;
import static util.Functions.addAnyTypeTask;
import static util.Functions.updateAnyTypeTask;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager getNewTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void updateTask_should_correct_validate_times() {
        // Валидность полей времени
        testTimesForUpdateTaskAndSubtaskType(getNewTaskManager(), TASK);
    }

    @Test
    void updateTask_should_be_correct_validating_for_time() {
        // Валидность полей времени
        testTimesForUpdateTaskAndSubtaskType(getNewTaskManager(), TASK);
    }

    @Test
    void updateSubtask_should_correct_validate_times() {
        // Валидность полей времени
        testTimesForUpdateTaskAndSubtaskType(getNewTaskManager(), SUBTASK);
    }

    @Test
    void updateSubtask_should_be_correct_validating_for_time() {
        // Валидность полей времени
        testTimesForUpdateTaskAndSubtaskType(getNewTaskManager(), SUBTASK);
    }

    @Test
    void getPrioritizedTasks() {
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Список не пустой");

        int timeStep = taskManager.getTimeStepByTimeManager();

        final Task task1 = new Task("Name", "Description", NEW);
        final Task task2 = new Task("Name", "Description", NEW);
        task1.setDuration(timeStep);
        task1.setStartTime(LocalDateTime.of(2022, 8, 5, 9, 0)); // 2
        task2.setDuration(timeStep);
        task2.setStartTime(LocalDateTime.of(2022, 8, 5, 8, 0)); // 1
        final int id1 = taskManager.addTask(task1);
        final int id2 = taskManager.addTask(task2);

        final Epic epic1 = new Epic("Name", "Description");
        final Epic epic2 = new Epic("Name", "Description");
        final int epicId1 = taskManager.addEpic(epic1);
        final int epicId2 = taskManager.addEpic(epic2);

        final Subtask subtask1 = new Subtask("Name", "Description", NEW, epicId1);
        final Subtask subtask2 = new Subtask("Name", "Description", IN_PROGRESS, epicId1);
        final Subtask subtask3 = new Subtask("Name", "Description", DONE, epicId2);
        subtask1.setDuration(timeStep);
        subtask1.setStartTime(LocalDateTime.of(2022, 8, 5, 7, 0)); // 0
        subtask2.setDuration(timeStep);
        subtask2.setStartTime(LocalDateTime.of(2022, 8, 5, 11, 0)); // 4
        subtask3.setDuration(timeStep);
        subtask3.setStartTime(LocalDateTime.of(2022, 8, 5, 10, 0)); // 3
        final int id3 = taskManager.addSubtask(subtask1);
        final int id4 = taskManager.addSubtask(subtask2);
        final int id5 = taskManager.addSubtask(subtask3);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(5, prioritizedTasks.size(), "Неверный размер списка");
        assertEquals(subtask1, prioritizedTasks.get(0), "Неверный порядок");
        assertEquals(task2, prioritizedTasks.get(1), "Неверный порядок");
        assertEquals(task1, prioritizedTasks.get(2), "Неверный порядок");
        assertEquals(subtask3, prioritizedTasks.get(3), "Неверный порядок");
        assertEquals(subtask2, prioritizedTasks.get(4), "Неверный порядок");

        //taskManager.getPrioritizedTasks().forEach(t -> System.out.println(t + "\n"));
    }

    private void testTimesForUpdateTaskAndSubtaskType(InMemoryTaskManager tm, TaskType type)
            throws IllegalArgumentException {
        Epic epic = new Epic("Name", "Description");
        Task task1;
        Task task2;
        Task newTask;
        switch (type) {
            case TASK:
                task1 = new Task("Name1", "Description1", NEW);
                task2 = new Task("Name2", "Description2", NEW);
                newTask = new Task("newTaskName", "newTaskDescription", NEW);
                break;
            case SUBTASK:
                addAnyTypeTask(tm, epic);
                task1 = new Subtask("Name1", "Description1", NEW, epic.getId());
                task2 = new Subtask("Name2", "Description2", NEW, epic.getId());
                newTask = new Subtask("newTaskName", "newTaskDescription", NEW, epic.getId());
                break;
            case EPIC:
                throw new IllegalArgumentException("Для типа " + type + " проверка не реализована");
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
        int id1 = addAnyTypeTask(tm, task1);
        int id2 = addAnyTypeTask(tm, task2);

        int timeStep = tm.getTimeManager().getTimeStep();
        LocalDateTime start = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                0,
                0);

        // Установим время у контрольной задачи
        newTask.setId(id1);
        newTask.setDuration(timeStep * 2L);
        newTask.setStartTime(start);
        updateAnyTypeTask(tm, newTask);

        // Внутри контрольной
        newTask.setId(id2);
        newTask.setDuration(timeStep);
        newTask.setStartTime(start);
        updateAnyTypeTask(tm, newTask);
        assertNotEquals(task2, newTask, "Задача записалась");

        //За границами года
        newTask.setDuration(timeStep * 3L);
        newTask.setStartTime(start.minusMinutes(timeStep));
        updateAnyTypeTask(tm, newTask);
        assertNotEquals(task2, newTask, "Задача опять записалась");

        // Старт внутри контрольной
        newTask.setDuration(timeStep * 4L);
        newTask.setStartTime(start.plusMinutes(timeStep));
        updateAnyTypeTask(tm, newTask);
        assertNotEquals(task2, newTask, "Задача еще раз записалась");

        // Нет пересечений с контрольной
        newTask.setDuration(timeStep);
        newTask.setStartTime(start.plusMinutes(timeStep * 2L));
        updateAnyTypeTask(tm, newTask);
        assertEquals(task2, newTask, "Задача не записалась");

        if (type == SUBTASK) { // У эпика расчетное время
            LocalDateTime startEp = task1.getStartTime().isBefore(task2.getStartTime()) ? task1.getStartTime() :
                    task2.getStartTime();
            LocalDateTime end = task1.getEndTime().isAfter(task2.getEndTime()) ? task1.getEndTime() :
                    task2.getEndTime();
            addAnyTypeTask(tm, new Subtask("Name1", "Description1", NEW, epic.getId()));

            assertEquals(startEp, epic.getStartTime(), "Время старта у Эпика не совпадает ");
            assertEquals(end, epic.getEndTime(), "Время окончания у Эпика не совпадает ");
            assertEquals(task1.getDuration() + task2.getDuration(), epic.getDuration(),
                    "Продолжительность у Эпика не совпадает ");
        }
    }

}