package ua.pustovalov.sigmatesttask.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.entity.Subject;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubjectMapper {

    SubjectDto convert(Subject subject);

    @Mapping(target = "id", ignore = true)
    Subject convert(SubjectDto subject);

    List<SubjectDto> convert(List<Subject> subjectList);

}
