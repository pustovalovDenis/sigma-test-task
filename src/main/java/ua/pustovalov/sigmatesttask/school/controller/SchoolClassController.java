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
import ua.pustovalov.sigmatesttask.school.controller.response.SchoolClassListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.SchoolClassResponse;
import ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto;
import ua.pustovalov.sigmatesttask.school.entity.SchoolClass;
import ua.pustovalov.sigmatesttask.school.filter.SchoolClassFilter;
import ua.pustovalov.sigmatesttask.school.service.SchoolClassService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/classes")
public class SchoolClassController {

    private final SchoolClassService service;

    /**
     * Create or Update {@link SchoolClass}
     * Dto must contain unique key of {@link SchoolClass}
     * for find and createOrUpdate it
     *
     * @param dto {@link ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto}
     *
     * @return {@link ResponseEntity}
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SchoolClassDto> createOrUpdate(
        @Valid @RequestBody SchoolClassDto dto) {
        service.createOrUpdate(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SchoolClassListResponse findAll(@Valid @RequestBody SchoolClassFilter filter) {
        return new SchoolClassListResponse(service.findAll(filter));
    }

    /**
     * Method return {@link SchoolClassResponse} with {@link SchoolClassDto}
     * by unique key.
     *
     * @param id unique key of {@link SchoolClass}
     *
     * @return {@link SchoolClassResponse}
     */
    @GetMapping(value = "/{id}")
    public SchoolClassResponse getByID(@PathVariable(value = "id") String id) {
        return new SchoolClassResponse(service.findById(Long.parseLong(id)));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteById(@PathVariable(value = "id") String id) {
        service.deleteById(Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
