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
public class StudentDto {

    private Long id;

    private String name;

    private String surname;

    private List<SubjectDto> subjects;

    private Boolean unplaced;

    private Integer level;

}
