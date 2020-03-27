package ua.pustovalov.sigmatesttask.school.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.pustovalov.sigmatesttask.school.controller.response.ErrorResponse;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;

import java.util.EnumMap;
import java.util.List;

/**
 * Exception handler
 *
 * @author Pustovalov Denis
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Repository for exception handler's
     */
    private EnumMap<ExceptionStatus, ServiceExceptionHandler> errorHandlers = new EnumMap<>(ExceptionStatus.class);

    /**
     * Method collect all {@link ServiceExceptionHandler}
     *
     * @param errorHandlers list with {@link ServiceExceptionHandler}
     */
    @Autowired
    public ControllerExceptionHandler(List<ServiceExceptionHandler> errorHandlers) {
        for (ServiceExceptionHandler errorHandler : errorHandlers) {
            this.errorHandlers.put(errorHandler.getHttpStatus(), errorHandler);
        }
    }

    /**
     * Exception handler
     *
     * @param exception {@link ServiceException}
     *
     * @return {@link ErrorResponse}
     */
    @ExceptionHandler({ ServiceException.class })
    @ResponseBody
    private ResponseEntity<ErrorResponse> exceptionHandler(ServiceException exception) {
        if (errorHandlers.containsKey(exception.getExceptionStatus())) {
            return errorHandlers.get(exception.getExceptionStatus()).handleException(exception);
        }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body(new ErrorResponse(ExceptionStatus.INTERNAL_SERVER_ERROR.getCode(), exception.getMessage()));
    }

}
