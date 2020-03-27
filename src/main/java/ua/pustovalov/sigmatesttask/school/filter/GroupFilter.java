package ua.pustovalov.sigmatesttask.school.filter;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupFilter {

    private Long id;

    private String caption;

    private Integer level;

}
