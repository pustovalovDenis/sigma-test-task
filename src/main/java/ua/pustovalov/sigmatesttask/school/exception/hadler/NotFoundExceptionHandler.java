package ua.pustovalov.sigmatesttask.school.exception.hadler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ua.pustovalov.sigmatesttask.school.controller.response.ErrorResponse;
import ua.pustovalov.sigmatesttask.school.exception.ServiceException;
import ua.pustovalov.sigmatesttask.school.exception.ServiceExceptionHandler;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;

/**
 * Exception handler for {@link ExceptionStatus#NOT_FOUND}
 *
 * @author Pustovalov Denis
 */
@Component
public class NotFoundExceptionHandler implements ServiceExceptionHandler {

    @Override
    public ExceptionStatus getHttpStatus() {
        return ExceptionStatus.NOT_FOUND;
    }

    @Override
    public ResponseEntity<ErrorResponse> handleException(ServiceException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND.value())
            .body(new ErrorResponse(ExceptionStatus.NOT_FOUND.getCode(), exception.getMessage()));
    }

}
