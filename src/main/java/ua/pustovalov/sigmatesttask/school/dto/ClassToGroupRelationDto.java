package ua.pustovalov.sigmatesttask.school.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClassToGroupRelationDto {

    private Long schoolClassId;

    private List<Long> groupIds;

}
