package ua.pustovalov.sigmatesttask.school.filter;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SubjectFilter {

    private Long id;

    private String caption;

    private List<Long> ids;

    private Boolean defaultSubject;

}
