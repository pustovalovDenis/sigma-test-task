package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.pustovalov.sigmatesttask.school.dto.ClassToGroupRelationDto;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.entity.ClassToGroupRelation;
import ua.pustovalov.sigmatesttask.school.entity.Group;
import ua.pustovalov.sigmatesttask.school.entity.SchoolClass;
import ua.pustovalov.sigmatesttask.school.exception.ServiceException;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;
import ua.pustovalov.sigmatesttask.school.mapper.GroupMapper;
import ua.pustovalov.sigmatesttask.school.repository.ClassToGroupRelationRepository;
import ua.pustovalov.sigmatesttask.school.repository.GroupRepository;
import ua.pustovalov.sigmatesttask.school.repository.SchoolClassRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClassToGroupRelationService {

    private final ClassToGroupRelationRepository repository;

    private final GroupRepository groupRepository;

    private final GroupMapper groupMapper;

    private final SchoolClassRepository schoolClassRepository;

    @Transactional
    public void createRelation(ClassToGroupRelationDto relation) {
        if (CollectionUtils.isNotEmpty(relation.getGroupIds()) && relation.getSchoolClassId() != null) {
            for (Long id : relation.getGroupIds()) {
                Optional<Group> group = groupRepository.findById(id);
                Optional<SchoolClass> entity = schoolClassRepository.findById(relation.getSchoolClassId());
                if (entity.isPresent() && group.isPresent()) {
                    repository.save(ClassToGroupRelation.builder()
                                                        .schoolClass(entity.get())
                                                        .group(group.get()).build());
                } else {
                    log.warn("Requested for create relation between class and group wasn't created - class id : {}" +
                                 " group id: {}. Group or class doesn't exists. ", relation.getSchoolClassId(), id);
                    throw new ServiceException(ExceptionStatus.NOT_FOUND);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllByGroupId(Long id) {
        repository.deleteAllByGroupId(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllBySchoolClassId(Long id) {
        repository.deleteAllBySchoolClassId(id);
    }

    @Transactional(readOnly = true)
    public List<GroupDto> getGroupsBySchoolClassId(Long id) {
        List<ClassToGroupRelation> relations = repository.findAllBySchoolClassId(id);
        if (CollectionUtils.isNotEmpty(relations)) {
            return groupMapper.convert(relations.stream().map(ClassToGroupRelation::getGroup).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }

}
