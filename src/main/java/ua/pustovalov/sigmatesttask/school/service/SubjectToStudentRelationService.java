package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.dto.SubjectToStudentRelationDto;
import ua.pustovalov.sigmatesttask.school.entity.Student;
import ua.pustovalov.sigmatesttask.school.entity.Subject;
import ua.pustovalov.sigmatesttask.school.entity.SubjectToStudentRelation;
import ua.pustovalov.sigmatesttask.school.exception.ServiceException;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;
import ua.pustovalov.sigmatesttask.school.mapper.SubjectMapper;
import ua.pustovalov.sigmatesttask.school.repository.StudentRepository;
import ua.pustovalov.sigmatesttask.school.repository.SubjectRepository;
import ua.pustovalov.sigmatesttask.school.repository.SubjectToStudentRelationRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectToStudentRelationService {

    private final SubjectToStudentRelationRepository repository;

    private final SubjectRepository subjectRepository;

    private final StudentRepository studentRepository;

    private final SubjectMapper subjectMapper;

    @Transactional
    public void createRelation(SubjectToStudentRelationDto relation) {
        if (CollectionUtils.isNotEmpty(relation.getSubjectIds()) && relation.getStudentId() != null) {
            for (Long subjectId : relation.getSubjectIds()) {
                Optional<Subject> subject = subjectRepository.findById(subjectId);
                Optional<Student> student = studentRepository.findById(relation.getStudentId());
                if (student.isPresent() && subject.isPresent()) {
                    repository.save(SubjectToStudentRelation.builder()
                                                            .student(student.get())
                                                            .subject(subject.get()).build());
                } else {
                    log.warn("Requested for create relation between student and subject wasn't created - student id : {}" +
                                 " subject id: {}. Student or subject doesn't exists. ", relation.getStudentId(), subjectId);
                    throw new ServiceException(ExceptionStatus.NOT_FOUND);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllByStudentId(Long id) {
        repository.deleteAllByStudentId(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllBySubjectId(Long id) {
        repository.deleteAllBySubjectId(id);
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getSubjectsByStudentId(Long id) {
        List<SubjectToStudentRelation> relations = repository.findAllByStudentId(id);
        if (CollectionUtils.isNotEmpty(relations)) {
            return subjectMapper.convert(relations.stream().map(SubjectToStudentRelation::getSubject).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
}
