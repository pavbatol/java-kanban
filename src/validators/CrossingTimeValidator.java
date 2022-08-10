package validators;

import exceptions.ValidateCrossingTimeException;
import exceptions.ValidateException;
import managers.TimeManager;
import tasks.Task;

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

        boolean isFree = timeManager.isFreeFor(task);
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


