package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;
import ua.pustovalov.sigmatesttask.school.entity.Student;
import ua.pustovalov.sigmatesttask.school.exception.ServiceException;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;
import ua.pustovalov.sigmatesttask.school.filter.StudentFilter;
import ua.pustovalov.sigmatesttask.school.mapper.StudentMapper;
import ua.pustovalov.sigmatesttask.school.repository.StudentRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for {@link ua.pustovalov.sigmatesttask.school.entity.Student}
 *
 * @author Pustovalov Denis
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;

    private final StudentMapper mapper;

    private final SubjectToStudentRelationService subjectToStudentRelationService;

    private final StudentToGroupRelationService studentToGroupRelationService;

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdate(StudentDto dto) {
        if (dto.getId() != null && !repository.existsById(dto.getId())) {
            throw new ServiceException(ExceptionStatus.NOT_FOUND);
        }
        Student domain = mapper.convert(dto);
        if (dto.getId() != null) {
            domain.setId(dto.getId());
        }
        repository.save(domain);
    }

    @Transactional(readOnly = true)
    public List<StudentDto> findAll(StudentFilter filter) {
        return mapper.convert(repository.findAllByFilter(filter)).stream().peek(this::prefetchRelation)
                     .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void prefetchRelation(StudentDto dto) {
        dto.setSubjects(subjectToStudentRelationService.getSubjectsByStudentId(dto.getId()));
    }

    @Transactional(readOnly = true)
    public StudentDto findById(Long id) {
        if (Objects.nonNull(id)) {
            Optional<Student> student = repository.findById(id);
            if (student.isPresent()) {
                StudentDto studentDto = mapper.convert(student.get());
                prefetchRelation(studentDto);
                return studentDto;
            }
            log.warn("Requested for getting student id was not found - id : {}", id);
            throw new ServiceException(ExceptionStatus.NOT_FOUND);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (Objects.nonNull(id)) {
            try {
                repository.deleteById(id);
                subjectToStudentRelationService.deleteAllByStudentId(id);
                studentToGroupRelationService.deleteAllByStudentId(id);
            } catch (EmptyResultDataAccessException e) {
                log.warn("Requested for deletion student id was not found - id:{}", id);
                throw new ServiceException(ExceptionStatus.NOT_FOUND);
            }
        }
    }

}
