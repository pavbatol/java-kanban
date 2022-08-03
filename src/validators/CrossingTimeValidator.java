package validators;

import exceptions.ValidateCrossingTimeException;
import exceptions.ValidateException;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

public class CrossingTimeValidator implements Validator {
    List<Task> forCheckTasks;

    public CrossingTimeValidator(List<Task> forCheckTasks) {
        this.forCheckTasks = forCheckTasks;
    }

    @Override
    public final <T extends Task> void validate(T task) throws ValidateException {
        if (task == null) {
            return;
        }
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return;
        }

        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = task.getEndTime();
        for (Task t: forCheckTasks) {
            if (t == null || t.getId() == task.getId()) {
                continue;
            }
            if (t.getStartTime() == null || t.getEndTime() == null) {
                continue;
            }
            LocalDateTime tStart = t.getStartTime();
            LocalDateTime tEnd = t.getEndTime();
            if ((taskStart.isEqual(tStart) || (taskStart.isAfter(tStart) && taskStart.isBefore(tEnd)))
                    || (taskEnd.isEqual(tEnd)  || (taskEnd.isBefore(tEnd) && taskEnd.isAfter(tStart)))) {
                throw new ValidateCrossingTimeException( "!!! " + getClass().getSimpleName() +
                        ": Недопустимо пересечение по времени.");
            }
        }
    }
}


