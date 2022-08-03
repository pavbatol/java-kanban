package validators;

import exceptions.ValidateDurationTimeException;
import exceptions.ValidateException;
import tasks.Task;

public class DurationTimeValidator implements Validator {
    @Override
    public <T extends Task> void validate(T task) throws ValidateException {
        if (task == null) {
            return;
        }
        if (task.getStartTime() == null) {
            return;
        }
        if (task.getDuration() <= 0) {
            throw new ValidateDurationTimeException("!!! " + getClass().getSimpleName() +
                    ": duration должен быть больше 0.");
        }
    }
}
