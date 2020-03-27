package ua.pustovalov.sigmatesttask.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDto {

    private Long id;

    private String caption;

    private Boolean defaultSubject;

}
