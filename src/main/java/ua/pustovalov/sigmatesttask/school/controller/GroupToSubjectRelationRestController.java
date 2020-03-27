package ua.pustovalov.sigmatesttask.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.pustovalov.sigmatesttask.school.dto.GroupToSubjectRelationDto;
import ua.pustovalov.sigmatesttask.school.service.GroupToSubjectRelationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groupToSubjects")
public class GroupToSubjectRelationRestController {

    private final GroupToSubjectRelationService service;

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createOrUpdate(
        @Valid @RequestBody GroupToSubjectRelationDto dto) {
        service.createRelation(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
