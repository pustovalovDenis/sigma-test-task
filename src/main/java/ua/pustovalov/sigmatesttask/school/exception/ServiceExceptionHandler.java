package ua.pustovalov.sigmatesttask.school.exception;

import org.springframework.http.ResponseEntity;
import ua.pustovalov.sigmatesttask.school.controller.response.ErrorResponse;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;

/**
 * interface for school exception handler repository
 *
 * @author Pustovalov Denis
 */
public interface ServiceExceptionHandler {

    ExceptionStatus getHttpStatus();

    ResponseEntity<ErrorResponse> handleException(ServiceException exception);

}
