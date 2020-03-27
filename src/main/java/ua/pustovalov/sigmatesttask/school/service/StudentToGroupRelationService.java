package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;
import ua.pustovalov.sigmatesttask.school.dto.StudentToGroupRelationDto;
import ua.pustovalov.sigmatesttask.school.entity.Group;
import ua.pustovalov.sigmatesttask.school.entity.Student;
import ua.pustovalov.sigmatesttask.school.entity.StudentToGroupRelation;
import ua.pustovalov.sigmatesttask.school.exception.ServiceException;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;
import ua.pustovalov.sigmatesttask.school.mapper.StudentMapper;
import ua.pustovalov.sigmatesttask.school.repository.GroupRepository;
import ua.pustovalov.sigmatesttask.school.repository.StudentRepository;
import ua.pustovalov.sigmatesttask.school.repository.StudentToGroupRelationRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentToGroupRelationService {

    private final StudentToGroupRelationRepository repository;

    private final StudentRepository studentRepository;

    private final StudentMapper studentMapper;

    private final GroupRepository groupRepository;

    @Transactional
    public void createRelation(StudentToGroupRelationDto relation) {
        if (CollectionUtils.isNotEmpty(relation.getStudentIds()) && relation.getGroupId() != null) {
            for (Long studentId : relation.getStudentIds()) {
                Optional<Student> student = studentRepository.findById(studentId);
                Optional<Group> group = groupRepository.findById(relation.getGroupId());
                if (group.isPresent() && student.isPresent()) {
                    repository.save(StudentToGroupRelation.builder()
                                                          .group(group.get())
                                                          .student(student.get()).build());
                } else {
                    log.warn("Requested for create relation between group and student wasn't created - group id : {}" +
                                 " student id: {}. Group or student doesn't exists. ", relation.getGroupId(), studentId);
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
    public void deleteAllByStudentId(Long id) {
        repository.deleteAllByStudentId(id);
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByGroupId(Long id) {
        List<StudentToGroupRelation> relations = repository.findAllByGroupId(id);
        if (CollectionUtils.isNotEmpty(relations)) {
            return studentMapper.convert(relations.stream().map(StudentToGroupRelation::getStudent).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }

}
