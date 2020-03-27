package ua.pustovalov.sigmatesttask.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.entity.Group;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupMapper {

    GroupDto convert(Group group);

    @Mapping(target = "id", ignore = true)
    Group convert(GroupDto group);

    List<GroupDto> convert(List<Group> groups);

}
