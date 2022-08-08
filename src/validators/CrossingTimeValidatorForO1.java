package validators;

import exceptions.ValidateCrossingTimeException;
import exceptions.ValidateException;
import managers.TimesKeeper;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

public class CrossingTimeValidatorForO1 implements Validator {
    List<Task> forCheckTasks;

    public CrossingTimeValidatorForO1(List<Task> forCheckTasks) {
        this.forCheckTasks = forCheckTasks;
    }

    @Override
    public final <T extends Task> void validate(T task) throws ValidateException {
        if (task == null) {
            throw new ValidateCrossingTimeException("!!! "+ getClass().getSimpleName() + ": Не могу проверить");
        }

        if (task.getStartTime() == null || task.getEndTime() == null) {
            throw new ValidateCrossingTimeException("!!! "+ getClass().getSimpleName() + ": Не могу проверить");
        }

        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = task.getEndTime();
        TimesKeeper timesKeeper = new TimesKeeper(15);
        boolean isFree = timesKeeper.isFree(taskStart, taskEnd);
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


