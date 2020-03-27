package ua.pustovalov.sigmatesttask.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.pustovalov.sigmatesttask.school.controller.response.GroupListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.GroupResponse;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.entity.Group;
import ua.pustovalov.sigmatesttask.school.filter.GroupFilter;
import ua.pustovalov.sigmatesttask.school.service.GroupService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupRestController {

    private final GroupService service;

    /**
     * Create or Update {@link ua.pustovalov.sigmatesttask.school.entity.Group}
     * Dto must contain unique key of {@link Group}
     * for find and createOrUpdate it
     *
     * @param group {@link ua.pustovalov.sigmatesttask.school.dto.GroupDto}
     *
     * @return {@link ResponseEntity}
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupDto> createOrUpdate(
        @Valid @RequestBody GroupDto group) {
        service.createOrUpdate(group);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GroupListResponse findAll(@Valid @RequestBody GroupFilter filter) {
        return new GroupListResponse(service.findAll(filter));
    }

    /**
     * Method return {@link GroupResponse} with {@link GroupDto}
     * by unique key.
     *
     * @param id unique key of {@link Group}
     *
     * @return {@link GroupResponse}
     */
    @GetMapping(value = "/{id}")
    public GroupResponse getByID(@PathVariable(value = "id") String id) {
        return new GroupResponse(service.findById(Long.parseLong(id)));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteById(@PathVariable(value = "id") String id) {
        service.deleteById(Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
