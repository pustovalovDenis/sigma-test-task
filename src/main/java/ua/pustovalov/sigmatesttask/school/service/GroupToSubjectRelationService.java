package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.pustovalov.sigmatesttask.school.dto.GroupToSubjectRelationDto;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.entity.Group;
import ua.pustovalov.sigmatesttask.school.entity.GroupToSubjectRelation;
import ua.pustovalov.sigmatesttask.school.entity.Subject;
import ua.pustovalov.sigmatesttask.school.exception.ServiceException;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;
import ua.pustovalov.sigmatesttask.school.mapper.SubjectMapper;
import ua.pustovalov.sigmatesttask.school.repository.GroupRepository;
import ua.pustovalov.sigmatesttask.school.repository.GroupToSubjectRelationRepository;
import ua.pustovalov.sigmatesttask.school.repository.SubjectRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupToSubjectRelationService {

    private final GroupToSubjectRelationRepository repository;

    private final SubjectRepository subjectRepository;

    private final SubjectMapper subjectMapper;

    private final GroupRepository groupRepository;

    @Transactional
    public void createRelation(GroupToSubjectRelationDto relation) {
        if (CollectionUtils.isNotEmpty(relation.getSubjectIds()) && relation.getGroupId() != null) {
            for (Long subjectId : relation.getSubjectIds()) {
                Optional<Subject> subject = subjectRepository.findById(subjectId);
                Optional<Group> group = groupRepository.findById(relation.getGroupId());
                if (group.isPresent() && subject.isPresent()) {
                    repository.save(GroupToSubjectRelation.builder()
                                                          .group(group.get())
                                                          .subject(subject.get()).build());
                } else {
                    log.warn("Requested for create relation between group and subject wasn't created - group id : {}" +
                                 " subject id: {}. Group or subject doesn't exists. ", relation.getGroupId(), subjectId);
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
    public void deleteAllBySubjectId(Long id) {
        repository.deleteAllBySubjectId(id);
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getSubjectsByGroupId(Long id) {
        List<GroupToSubjectRelation> relations = repository.findAllByGroupId(id);
        if (CollectionUtils.isNotEmpty(relations)) {
            return subjectMapper.convert(relations.stream().map(GroupToSubjectRelation::getSubject).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
}
