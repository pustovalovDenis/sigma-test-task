package ua.pustovalov.sigmatesttask.school.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;

/**
 * Common exception for all services
 *
 * @author Pustovalov Denis
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException {

    private final ExceptionStatus exceptionStatus;

    public ServiceException(ExceptionStatus exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

}
