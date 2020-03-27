package ua.pustovalov.sigmatesttask.school.exception.dict;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Dict with exception codes.
 *
 * @author Pustovalov Denis
 */
public enum ExceptionStatus {

    INTERNAL_SERVER_ERROR("internalservererror", "Internal Server Error"),
    NOT_FOUND("notfound", "Not Found");

    private final String code;

    private final String description;

    ExceptionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(code);
    }
}
