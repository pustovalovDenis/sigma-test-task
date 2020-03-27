package ua.pustovalov.sigmatesttask.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.entity.Group;
import ua.pustovalov.sigmatesttask.school.exception.ServiceException;
import ua.pustovalov.sigmatesttask.school.exception.dict.ExceptionStatus;
import ua.pustovalov.sigmatesttask.school.filter.GroupFilter;
import ua.pustovalov.sigmatesttask.school.mapper.GroupMapper;
import ua.pustovalov.sigmatesttask.school.repository.GroupRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for {@link ua.pustovalov.sigmatesttask.school.entity.Group}
 *
 * @author Pustovalov Denis
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository repository;

    private final GroupMapper mapper;

    private final GroupToSubjectRelationService groupToSubjectRelationService;

    private final StudentToGroupRelationService studentToGroupRelationService;

    private final ClassToGroupRelationService classToGroupRelationService;

    private final StudentService studentService;

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdate(GroupDto dto) {
        if (dto.getId() != null && !repository.existsById(dto.getId())) {
            throw new ServiceException(ExceptionStatus.NOT_FOUND);
        }
        repository.save(mapper.convert(dto));
    }

    @Transactional(readOnly = true)
    public List<GroupDto> findAll(GroupFilter filter) {
        return mapper.convert(repository.findAllByFilter(filter)).stream().peek(this::prefetchRelation)
                     .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void prefetchRelation(GroupDto dto) {
        dto.setSubjects(groupToSubjectRelationService.getSubjectsByGroupId(dto.getId()));
        dto.setStudents(studentToGroupRelationService.getStudentsByGroupId(dto.getId()));
        dto.getStudents().forEach(studentService::prefetchRelation);
    }

    @Transactional(readOnly = true)
    public GroupDto findById(Long id) {
        if (Objects.nonNull(id)) {
            Optional<Group> group = repository.findById(id);
            if (group.isPresent()) {
                GroupDto groupDto = mapper.convert(group.get());
                prefetchRelation(groupDto);
                return groupDto;
            }
            log.warn("Requested for getting group id was not found - id : {}", id);
            throw new ServiceException(ExceptionStatus.NOT_FOUND);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (Objects.nonNull(id)) {
            try {
                repository.deleteById(id);
                groupToSubjectRelationService.deleteAllByGroupId(id);
                studentToGroupRelationService.deleteAllByGroupId(id);
                classToGroupRelationService.deleteAllByGroupId(id);
            } catch (EmptyResultDataAccessException e) {
                log.warn("Requested for deletion group id was not found - id:{}", id);
                throw new ServiceException(ExceptionStatus.NOT_FOUND);
            }
        }
    }

}
