package ua.pustovalov.sigmatesttask.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {

    private Long id;

    private String caption;

    private Integer minCapacity;

    private Integer maxCapacity;

    private List<SubjectDto> subjects;

    private List<StudentDto> students;

    private Integer level;

}
