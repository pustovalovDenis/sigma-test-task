package ua.pustovalov.sigmatesttask.school.filter;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentFilter {

    private Long id;

    private String name;

    private String surname;

    private Boolean unplaced;

    private Integer level;

}
