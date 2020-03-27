package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ua.pustovalov.sigmatesttask.school.dto.ClassToGroupRelationDto;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolClassesToGroupAssignorProcessor {

    private final ClassToGroupRelationService classToGroupRelationService;

    public void assignGroupsToClasses(List<SchoolClassDto> classes, List<GroupDto> groups) {
        if (CollectionUtils.isNotEmpty(groups)) {
            groups.forEach(group -> {
                for (SchoolClassDto schoolClass : classes) {
                    if (schoolClass.getLevels().contains(group.getLevel())) {
                        if (schoolClass.getGroups() == null) {
                            schoolClass.setGroups(new ArrayList<>());
                        }
                        schoolClass.getGroups().add(group);
                        classToGroupRelationService.createRelation(ClassToGroupRelationDto.builder()
                                                                                          .schoolClassId(schoolClass.getId())
                                                                                          .groupIds(Arrays.asList(group.getId()))
                                                                                          .build());
                    }
                    break;
                }
            });
        }
    }

}
