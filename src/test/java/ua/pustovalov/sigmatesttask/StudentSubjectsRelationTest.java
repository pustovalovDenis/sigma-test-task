package ua.pustovalov.sigmatesttask;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ua.pustovalov.sigmatesttask.school.controller.response.StudentListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.StudentResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.SubjectListResponse;
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.dto.SubjectToStudentRelationDto;
import ua.pustovalov.sigmatesttask.school.filter.StudentFilter;
import ua.pustovalov.sigmatesttask.school.filter.SubjectFilter;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentSubjectsRelationTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * This test create student and subjects, than create relation between and check that everything fine.
     */
    @Test
    @SneakyThrows
    public void createStudentsSubjectRelation() {
        StudentDto student = StudentDto.builder().name("Denis").surname("PustovalovSt").level(1).build();
        saveOrUpdateStudent(student);
        saveOrUpdateSubject(SubjectDto.builder().caption("math").build());
        saveOrUpdateSubject(SubjectDto.builder().caption("geography").defaultSubject(true).build());

        List<SubjectDto> subjects = getSubjectsByFilter(SubjectFilter.builder().build());

        List<StudentDto> students = getStudentsByFilter(StudentFilter
                                                            .builder()
                                                            .name(student.getName())
                                                            .surname(student.getSurname()).build()
        );
        assertEquals(1, students.size());

        createStudentSubjectRelations(SubjectToStudentRelationDto
                                          .builder()
                                          .studentId(students.get(0).getId())
                                          .subjectIds(subjects.stream().map(SubjectDto::getId).collect(Collectors.toList()))
                                          .build());
        StudentDto studentDto = getStudent(students.get(0).getId());
        assertNotNull(studentDto);
        assertEquals(2, studentDto.getSubjects().size());
    }

    @SneakyThrows
    private List<StudentDto> getStudentsByFilter(StudentFilter filter) {
        String contentAsString = mockMvc.perform(
            get("/students")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(filter))).andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, StudentListResponse.class).getStudents();
    }

    @SneakyThrows
    private void saveOrUpdateStudent(StudentDto student) {
        mockMvc.perform(
            put("/students")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(student)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @SneakyThrows
    private void createStudentSubjectRelations(SubjectToStudentRelationDto relationDto) {
        mockMvc.perform(
            put("/subjectsToStudent")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(relationDto)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @SneakyThrows
    private void saveOrUpdateSubject(SubjectDto subject) {
        mockMvc.perform(
            put("/subjects")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.getMapper().writeValueAsString(subject)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @SneakyThrows
    private StudentDto getStudent(Long id) {
        String contentAsString = mockMvc.perform(
            get("/students/" + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, StudentResponse.class).getStudent();
    }

    @SneakyThrows
    private List<SubjectDto> getSubjectsByFilter(SubjectFilter filter) {
        String contentAsString = mockMvc.perform(
            get("/subjects")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(SubjectFilter
                                                                 .builder()
                                                                 .build()
                ))).andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, SubjectListResponse.class).getSubjects();
    }

    @Test
    @SneakyThrows
    public void deleteSubjectAndCheckRelation() {
        StudentDto student = StudentDto.builder().name("Denis").surname("Pustovalovas").level(1).build();
        saveOrUpdateStudent(student);
        saveOrUpdateSubject(SubjectDto.builder().caption("math").build());
        saveOrUpdateSubject(SubjectDto.builder().caption("geography").defaultSubject(true).build());
        List<SubjectDto> subjects = getSubjectsByFilter(SubjectFilter.builder().build());
        assertEquals(2, subjects.size());
        List<StudentDto> students = getStudentsByFilter(StudentFilter
                                                            .builder()
                                                            .name(student.getName())
                                                            .surname(student.getSurname()).build());
        assertEquals(1, students.size());
        createStudentSubjectRelations(SubjectToStudentRelationDto
                                          .builder()
                                          .studentId(students.get(0).getId())
                                          .subjectIds(subjects.stream().map(SubjectDto::getId).collect(Collectors.toList()))
                                          .build());
        students = getStudentsByFilter(StudentFilter.builder().build());
        assertEquals(1, students.size());
        assertEquals(2, students.get(0).getSubjects().size());
        deleteSubject(subjects.get(1).getId());
        students = getStudentsByFilter(StudentFilter.builder().build());
        assertEquals(1, students.size());
        assertEquals(1, students.get(0).getSubjects().size());
    }

    @SneakyThrows
    private void deleteSubject(Long id) {
        this.mockMvc.perform(delete("/subjects/" + id).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

    }

    public void deleteRelationBySubject() {

    }

    public void deleteRelationByStudent() {

    }

}
