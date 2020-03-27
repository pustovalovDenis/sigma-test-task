package ua.pustovalov.sigmatesttask.school.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectListResponse extends CommonResponse {

    private List<SubjectDto> subjects;

}
