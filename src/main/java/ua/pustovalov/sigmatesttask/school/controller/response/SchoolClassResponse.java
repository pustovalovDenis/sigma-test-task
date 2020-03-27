package ua.pustovalov.sigmatesttask.school.controller.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolClassResponse extends CommonResponse {

    private SchoolClassDto classDto;

}
