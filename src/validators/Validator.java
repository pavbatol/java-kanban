package validators;

import exceptions.ValidateException;
import tasks.Task;

public interface Validator {
    <T extends Task> void validate(T task) throws ValidateException;
}
