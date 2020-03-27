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
import ua.pustovalov.sigmatesttask.school.controller.response.GroupListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.GroupResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.StudentListResponse;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;
import ua.pustovalov.sigmatesttask.school.dto.StudentToGroupRelationDto;
import ua.pustovalov.sigmatesttask.school.filter.GroupFilter;
import ua.pustovalov.sigmatesttask.school.filter.StudentFilter;

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
public class StudentToGroupRelationServiceTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void createStudentsGroupRelation() {
        StudentDto student = StudentDto.builder().name("Denis").surname("Agaass").level(1).build();
        saveOrUpdateStudent(student);
        saveOrUpdateStudent(StudentDto.builder().name("Oleg").surname("Vinniks").level(1).build());
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-1").level(1).build());
        List<GroupDto> groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(1, groups.size());
        createStudentGroupRelations(StudentToGroupRelationDto
                                        .builder()
                                        .groupId(groups.get(0).getId())
                                        .studentIds(getStudentsByFilter(StudentFilter.builder().build())
                                                        .stream().map(StudentDto::getId)
                                                        .collect(Collectors.toList()))
                                        .build());
        GroupDto group = getGroup(groups.get(0).getId());
        assertNotNull(group);
        assertEquals(2, group.getStudents().size());
    }

    @SneakyThrows
    private void createStudentGroupRelations(StudentToGroupRelationDto relationDto) {
        mockMvc.perform(
            put("/studentsToGroup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(relationDto)))
               .andDo(print())
               .andExpect(status().isOk());
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
    private GroupDto getGroup(Long id) {
        String contentAsString = mockMvc.perform(
            get("/groups/" + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, GroupResponse.class).getGroup();
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
    private void saveOrUpdateGroup(GroupDto group) {
        mockMvc.perform(
            put("/groups")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(group)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @SneakyThrows
    private List<GroupDto> getGroupsByFilter(GroupFilter filter) {
        String contentAsString = mockMvc.perform(
            get("/groups")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(filter))).andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, GroupListResponse.class).getGroups();
    }

    @Test
    @SneakyThrows
    public void deleteStudentAndCheckRelation() {
        StudentDto student = StudentDto.builder().name("Denis").surname("Pustovaalovgs").level(1).build();
        saveOrUpdateStudent(student);
        saveOrUpdateStudent(StudentDto.builder().name("Oleg").surname("Vinnika").level(1).build());
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-1").level(1).build());

        List<GroupDto> groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(1, groups.size());
        List<StudentDto> students = getStudentsByFilter(StudentFilter.builder().build());
        assertEquals(2, students.size());
        createStudentGroupRelations(StudentToGroupRelationDto
                                        .builder()
                                        .groupId(groups.get(0).getId())
                                        .studentIds(students.stream().map(StudentDto::getId).collect(Collectors.toList()))
                                        .build());
        groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(1, groups.size());
        assertEquals(2, groups.get(0).getStudents().size());
        deleteStudent(students.get(0).getId());
        groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(1, groups.size());
        assertEquals(1, groups.get(0).getStudents().size());
    }

    @SneakyThrows
    private void deleteStudent(Long id) {
        this.mockMvc.perform(delete("/students/" + id).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

    }

}
