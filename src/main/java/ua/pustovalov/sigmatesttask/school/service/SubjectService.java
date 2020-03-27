package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.entity.Subject;
import ua.pustovalov.sigmatesttask.school.exception.ServiceException;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;
import ua.pustovalov.sigmatesttask.school.filter.SubjectFilter;
import ua.pustovalov.sigmatesttask.school.mapper.SubjectMapper;
import ua.pustovalov.sigmatesttask.school.repository.SubjectRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Service for {@link ua.pustovalov.sigmatesttask.school.entity.Subject}
 *
 * @author Pustovalov Denis
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;

    private final SubjectMapper mapper;

    private final SubjectToStudentRelationService studentRelationService;
    private final GroupToSubjectRelationService groupRelationService;

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdate(SubjectDto dto) {
        if (dto.getId() != null && !repository.existsById(dto.getId())) {
            throw new ServiceException(ExceptionStatus.NOT_FOUND);
        }
        repository.save(mapper.convert(dto));
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> findAll(SubjectFilter filter) {
        return mapper.convert(repository.findAllByFilter(filter));
    }

    @Transactional(readOnly = true)
    public SubjectDto findById(Long id) {
        if (Objects.nonNull(id)) {
            Optional<Subject> subject = repository.findById(id);
            if (subject.isPresent()) {
                return mapper.convert(subject.get());
            }
            log.warn("Requested for getting subject id was not found - id:{}", id);
            throw new ServiceException(ExceptionStatus.NOT_FOUND);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void deleteById(Long id) {
        if (Objects.nonNull(id)) {
            try {
                repository.deleteById(id);
                studentRelationService.deleteAllBySubjectId(id);
                groupRelationService.deleteAllBySubjectId(id);
            } catch (EmptyResultDataAccessException e) {
                log.warn("Requested for deletion subject id was not found - id:{}", id);
                throw new ServiceException(ExceptionStatus.NOT_FOUND);
            }
        }
    }
}
