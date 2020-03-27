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
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;
import ua.pustovalov.sigmatesttask.school.filter.StudentFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void saveOrUpdateStudentTest() {
        prepareStudent(StudentDto.builder().name("Denis").surname("Altal").level(1).build());
    }

    @SneakyThrows
    private void prepareStudent(StudentDto student) {
        mockMvc.perform(
            put("/students")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(student)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void checkFindAllTest() {
        prepareStudent(StudentDto.builder().name("Denis").surname("Remsas").level(1).build());
        prepareStudent(StudentDto.builder().name("Olegs").surname("Kadkaf").level(1).build());
        String contentAsString = mockMvc.perform(
            get("/students")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(StudentFilter
                                                                 .builder()
                                                                 .build()
                )))
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();
        List<StudentDto> students = this.getMapper().readValue(contentAsString, StudentListResponse.class).getStudents();
        assertEquals(2, students.size());
    }

    @Test
    @SneakyThrows
    public void checkFindByIdTest() {
        StudentDto expected = StudentDto.builder().name("Denis").surname("Trevor").level(4).unplaced(true).build();
        prepareStudent(expected);
        List<StudentDto> students = getStudentsByFilter(StudentFilter.builder().build());
        assertEquals(1, students.size());
        StudentDto actual = getStudent(students.get(0).getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getSurname(), actual.getSurname());
        assertEquals(expected.getLevel(), actual.getLevel());
        assertEquals(expected.getUnplaced(), actual.getUnplaced());
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
    private StudentDto getStudent(Long id) {
        String contentAsString = mockMvc.perform(
            get("/students/" + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, StudentResponse.class).getStudent();
    }

    @Test
    @SneakyThrows
    public void checkFindByFilter() {
        StudentDto expected = StudentDto.builder().name("Denis").surname("Creasta").level(4).unplaced(true).build();
        prepareStudent(expected);
        List<StudentDto> students = getStudentsByFilter(StudentFilter.builder().build());
        assertEquals(1, students.size());
        students = getStudentsByFilter(StudentFilter.builder().level(3).build());
        assertEquals(0, students.size());
        students = getStudentsByFilter(StudentFilter.builder().level(4).build());
        assertEquals(1, students.size());

        students = getStudentsByFilter(StudentFilter.builder().surname("unknown").build());
        assertEquals(0, students.size());
        students = getStudentsByFilter(StudentFilter.builder().surname("Creasta").build());
        assertEquals(1, students.size());

        students = getStudentsByFilter(StudentFilter.builder().name("unknown").build());
        assertEquals(0, students.size());
        students = getStudentsByFilter(StudentFilter.builder().name("Denis").build());
        assertEquals(1, students.size());

        students = getStudentsByFilter(StudentFilter.builder().unplaced(false).build());
        assertEquals(0, students.size());
        students = getStudentsByFilter(StudentFilter.builder().unplaced(true).build());
        assertEquals(1, students.size());
    }

    @Test
    @SneakyThrows
    public void checkDeleteByIdTest() {
        prepareStudent(StudentDto.builder().name("Denis").surname("Vass").level(1).build());
        List<StudentDto> students = getStudentsByFilter(StudentFilter.builder().build());
        assertEquals(1, students.size());
        deleteStudent(students.get(0).getId());
        students = getStudentsByFilter(StudentFilter.builder().build());
        assertEquals(0, students.size());
    }

    @SneakyThrows
    private void deleteStudent(Long id) {
        this.mockMvc.perform(delete("/students/" + id).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

    }

    @Test
    @SneakyThrows
    public void checkDeleteWithWrongIdTest() {
        this.mockMvc.perform(delete("/students/" + 111L).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
    }

}
