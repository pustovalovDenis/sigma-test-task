package ua.pustovalov.sigmatesttask.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto;
import ua.pustovalov.sigmatesttask.school.entity.SchoolClass;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SchoolClassMapper {

    SchoolClassDto convert(SchoolClass schoolClass);

    @Mapping(target = "id", ignore = true)
    SchoolClass convert(SchoolClassDto schoolClass);

    List<SchoolClassDto> convert(List<SchoolClass> schoolClasses);

}
