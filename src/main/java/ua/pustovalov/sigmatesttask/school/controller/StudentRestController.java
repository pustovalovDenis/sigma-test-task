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
import ua.pustovalov.sigmatesttask.school.controller.response.StudentListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.StudentResponse;
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;
import ua.pustovalov.sigmatesttask.school.entity.Student;
import ua.pustovalov.sigmatesttask.school.filter.StudentFilter;
import ua.pustovalov.sigmatesttask.school.service.StudentService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentRestController {

    private final StudentService service;

    /**
     * Create or Update {@link Student}
     * Dto must contain unique key of {@link Student}
     * for find and createOrUpdateWStudent it
     *
     * @param student {@link ua.pustovalov.sigmatesttask.school.dto.StudentDto}
     *
     * @return {@link ResponseEntity}
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> createOrUpdate(
        @Valid @RequestBody StudentDto student) {
        service.createOrUpdate(student);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentListResponse findAll(@Valid @RequestBody StudentFilter filter) {
        return new StudentListResponse(service.findAll(filter));
    }

    /**
     * Method return {@link StudentResponse} with {@link StudentDto}
     * by unique key.
     *
     * @param id unique key of {@link Student}
     *
     * @return {@link StudentResponse}
     */
    @GetMapping(value = "/{id}")
    public StudentResponse getByID(@PathVariable(value = "id") String id) {
        return new StudentResponse(service.findById(Long.parseLong(id)));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteById(@PathVariable(value = "id") String id) {
        service.deleteById(Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
