package validators;

import exceptions.ValidateCrossingTimeException;
import exceptions.ValidateException;
import managers.TimeManager;
import tasks.Task;

import java.time.LocalDateTime;

public class CrossingTimeValidator implements Validator {
    TimeManager timeManager;
    public CrossingTimeValidator(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    @Override
    public final <T extends Task> void validate(T task) throws ValidateException {
        if (task == null) {
            throw new ValidateCrossingTimeException("!!! "+ getClass().getSimpleName() + ": Не могу проверить");
        }

        if (task.getStartTime() == null && task.getEndTime() == null) {
            return;
        } else if (task.getStartTime() == null || task.getEndTime() == null) {
            throw new ValidateCrossingTimeException("!!! "+ getClass().getSimpleName() + ": Не могу проверить");
        }

        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = task.getEndTime();
        boolean isFree = timeManager.isFree(taskStart, taskEnd);
        if (!isFree) {
            throw new ValidateCrossingTimeException(
                    "!!! "
                    + getClass().getSimpleName()
                    + ": "
                    + "Недопустимо пересечение по времени."
            );
        }
    }
}


