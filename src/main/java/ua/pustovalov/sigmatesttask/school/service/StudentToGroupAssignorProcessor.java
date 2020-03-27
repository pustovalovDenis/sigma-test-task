package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.stereotype.Service;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;
import ua.pustovalov.sigmatesttask.school.dto.StudentToGroupRelationDto;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.kafka.KafkaProducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentToGroupAssignorProcessor {

    private final StudentToGroupRelationService studentToGroupRelationService;

    private final StudentService studentService;

    private final KafkaProducer kafkaProducer;

    public void assignStudentsToGroup(List<StudentDto> students, List<GroupDto> groups) {
        for (StudentDto studentDto : students) {
            List<GroupDto> groupDtoList = groups.stream().filter(val -> val.getLevel().equals(studentDto.getLevel())).collect(Collectors.toList());
            Map<Integer, Set<Long>> intersectionObjects = new TreeMap<>();
            for (GroupDto groupDto : groupDtoList) {
                List<String> intersection = studentDto.getSubjects().stream().map(SubjectDto::getCaption).collect(Collectors.toList());
                intersection.retainAll(groupDto.getSubjects().stream().map(SubjectDto::getCaption).collect(Collectors.toList()));
                Integer intersectionCount = intersection.size();
                Set<Long> intersectionObject = intersectionObjects.get(intersectionCount);
                if (intersectionObject == null) {
                    intersectionObjects.put(intersectionCount, Collections.asSet(groupDto.getId()));
                } else {
                    intersectionObject.add(groupDto.getId());
                }
            }
            putStudentIntoGroupAndCheckForUnplaced(studentDto, groupDtoList, intersectionObjects);
        }
    }

    private void putStudentIntoGroupAndCheckForUnplaced(StudentDto studentDto, List<GroupDto> groups, Map<Integer, Set<Long>> intersectionObjects) {
        if (MapUtils.isNotEmpty(intersectionObjects)) {
            Set<Long> groupsIds = intersectionObjects.get(intersectionObjects.keySet().stream().max(Integer::compare).get());
            if (CollectionUtils.isEmpty(groupsIds) || groupsIds.size() > 1) {
                studentDto.setUnplaced(true);
            } else {
                // Element should be always present, so if get return exception it's mean that something wrong and we catch bug
                GroupDto groupDto = groups.stream().filter(val -> val.getId().equals(new ArrayList<>(groupsIds).get(0))).findFirst().get();
                if (CollectionUtils.isNotEmpty(groupDto.getStudents())
                    && groupDto.getMaxCapacity() != null
                    && groupDto.getStudents().size() >= groupDto.getMaxCapacity()) {
                    studentDto.setUnplaced(true);
                } else {
                    if (CollectionUtils.isEmpty(groupDto.getStudents())) {
                        groupDto.setStudents(new ArrayList<>());
                    }
                    groupDto.getStudents().add(studentDto);
                    studentToGroupRelationService.createRelation(StudentToGroupRelationDto
                                                                     .builder()
                                                                     .groupId(groupDto.getId())
                                                                     .studentIds(Arrays.asList(studentDto.getId()))
                                                                     .build());
                }
            }
            if (BooleanUtils.isTrue(studentDto.getUnplaced())) {
                studentService.createOrUpdate(studentDto);
                kafkaProducer.sendMessageToHeadmaster(studentDto);
            }
        }
    }

}
