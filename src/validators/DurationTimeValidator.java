package validators;

import exceptions.ValidateDurationTimeException;
import exceptions.ValidateException;
import managers.TimeManager;
import tasks.Task;

public class DurationTimeValidator implements Validator {

    TimeManager timeManager;
    public DurationTimeValidator(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    @Override
    public <T extends Task> void validate(T task) throws ValidateException {
        if (task == null) {
            return;
        }
        if (task.getStartTime() == null) {
            return;
        }
        if (timeManager == null) {
            throw new ValidateDurationTimeException("!!! "+ getClass().getSimpleName() + ": Не могу проверить");
        }
        if (task.getDuration() <= 0 || task.getDuration() % timeManager.getTimeStep() != 0) {
            throw new ValidateDurationTimeException(
                    "!!! "
                    + getClass().getSimpleName()
                    + ": "
                    + "duration должен быть больше 0 и кратно " + timeManager.getTimeStep() + ".");
        }
    }
}
