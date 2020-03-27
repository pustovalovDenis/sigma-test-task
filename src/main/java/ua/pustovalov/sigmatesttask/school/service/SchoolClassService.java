package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto;
import ua.pustovalov.sigmatesttask.school.entity.SchoolClass;
import ua.pustovalov.sigmatesttask.school.exception.ServiceException;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;
import ua.pustovalov.sigmatesttask.school.filter.SchoolClassFilter;
import ua.pustovalov.sigmatesttask.school.mapper.SchoolClassMapper;
import ua.pustovalov.sigmatesttask.school.repository.SchoolClassRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for {@link ua.pustovalov.sigmatesttask.school.entity.SchoolClass}
 *
 * @author Pustovalov Denis
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SchoolClassService {

    private final SchoolClassRepository repository;

    private final SchoolClassMapper mapper;

    private final ClassToGroupRelationService classToGroupRelationService;

    private final GroupService groupService;

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdate(SchoolClassDto dto) {
        if (dto.getId() != null && !repository.existsById(dto.getId())) {
            throw new ServiceException(ExceptionStatus.NOT_FOUND);
        }
        repository.save(mapper.convert(dto));
    }

    @Transactional(readOnly = true)
    public List<SchoolClassDto> findAll(SchoolClassFilter filter) {
        return mapper.convert(repository.findAllByFilter(filter)).stream().peek(this::prefetchRelation)
                     .collect(Collectors.toList());
    }

    private void prefetchRelation(SchoolClassDto dto) {
        dto.setGroups(classToGroupRelationService.getGroupsBySchoolClassId(dto.getId()));
        dto.getGroups().forEach(groupService::prefetchRelation);
    }

    @Transactional(readOnly = true)
    public SchoolClassDto findById(Long id) {
        if (Objects.nonNull(id)) {
            Optional<SchoolClass> entity = repository.findById(id);
            if (entity.isPresent()) {
                SchoolClassDto dto = mapper.convert(entity.get());
                prefetchRelation(dto);
                return dto;
            }
            log.warn("Requested for getting class id was not found - id : {}", id);
            throw new ServiceException(ExceptionStatus.NOT_FOUND);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (Objects.nonNull(id)) {
            try {
                repository.deleteById(id);
                classToGroupRelationService.deleteAllBySchoolClassId(id);
            } catch (EmptyResultDataAccessException e) {
                log.warn("Requested for deletion class id was not found - id:{}", id);
                throw new ServiceException(ExceptionStatus.NOT_FOUND);
            }
        }
    }

}
