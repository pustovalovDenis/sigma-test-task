package ua.pustovalov.sigmatesttask.school.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentToGroupRelationDto {

    private Long groupId;

    private List<Long> studentIds;

}
