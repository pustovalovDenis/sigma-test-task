package ua.pustovalov.sigmatesttask.school.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolClassListResponse extends CommonResponse {

    private List<SchoolClassDto> classes;

}
