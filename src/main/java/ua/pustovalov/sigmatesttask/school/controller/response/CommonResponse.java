package ua.pustovalov.sigmatesttask.school.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Common response for all rest endpoint.
 *
 * @author Pustovalov Denis
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse {

    private Instant responseTime = Instant.now();

}
