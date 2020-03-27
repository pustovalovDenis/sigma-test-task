package ua.pustovalov.sigmatesttask.school.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends CommonResponse {

    private String errorCode;

    private String descriptionCode;

    public ErrorResponse(String errorCode, String descriptionCode) {
        this.errorCode = errorCode;
        this.descriptionCode = descriptionCode;
    }

}
