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
import ua.pustovalov.sigmatesttask.school.controller.response.SubjectListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.SubjectResponse;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.entity.Subject;
import ua.pustovalov.sigmatesttask.school.filter.SubjectFilter;
import ua.pustovalov.sigmatesttask.school.service.SubjectService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subjects")
public class SubjectRestController {

    private final SubjectService service;

    /**
     * Create or Update {@link ua.pustovalov.sigmatesttask.school.entity.Subject}
     * Dto must contain unique key of {@link Subject}
     * for find and createOrUpdateSubject it
     *
     * @param subject {@link ua.pustovalov.sigmatesttask.school.dto.SubjectDto}
     *
     * @return {@link ResponseEntity}
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubjectDto> createOrUpdate(
        @Valid @RequestBody SubjectDto subject) {
        service.createOrUpdate(subject);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SubjectListResponse findAll(@Valid @RequestBody SubjectFilter filter) {
        return new SubjectListResponse(service.findAll(filter));
    }

    /**
     * Method return {@link SubjectResponse} with {@link SubjectDto}
     * by unique key.
     *
     * @param id unique key of {@link Subject}
     *
     * @return {@link SubjectResponse}
     */
    @GetMapping(value = "/{id}")
    public SubjectResponse getByID(@PathVariable(value = "id") String id) {
        return new SubjectResponse(service.findById(Long.parseLong(id)));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteById(@PathVariable(value = "id") String id) {
        service.deleteById(Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
