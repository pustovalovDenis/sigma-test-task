package ua.pustovalov.sigmatesttask.school.filter;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SchoolClassFilter {

    private Long id;

    private String caption;

}
