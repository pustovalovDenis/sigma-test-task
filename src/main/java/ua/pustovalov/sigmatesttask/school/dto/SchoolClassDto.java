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
public class SchoolClassDto {

    private Long id;

    private String caption;

    private List<Integer> levels;

    private List<GroupDto> groups;

}
