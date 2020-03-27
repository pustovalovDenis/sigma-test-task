package ua.pustovalov.sigmatesttask.school.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubjectToStudentRelationDto {

    private Long studentId;

    private List<Long> subjectIds;

}
