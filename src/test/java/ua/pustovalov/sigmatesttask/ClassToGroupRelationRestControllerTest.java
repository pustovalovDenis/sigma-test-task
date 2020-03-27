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
import ua.pustovalov.sigmatesttask.school.controller.response.SchoolClassListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.SchoolClassResponse;
import ua.pustovalov.sigmatesttask.school.dto.ClassToGroupRelationDto;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto;
import ua.pustovalov.sigmatesttask.school.filter.GroupFilter;
import ua.pustovalov.sigmatesttask.school.filter.SchoolClassFilter;

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
public class ClassToGroupRelationRestControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * This test create groups and class, than create relation between and check that everything fine.
     */
    @Test
    @SneakyThrows
    public void createGroupsClassRelation() {
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-1").level(1).build());
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-2").level(1).build());
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-3").level(1).build());
        saveOrUpdateSchoolClass(SchoolClassDto.builder().caption("305").build());

        List<SchoolClassDto> classes = getSchoolClassesByFilter(SchoolClassFilter.builder().build());
        assertEquals(1, classes.size());
        List<GroupDto> groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(3, groups.size());

        createClassToGroupRelations(ClassToGroupRelationDto
                                        .builder()
                                        .schoolClassId(classes.get(0).getId())
                                        .groupIds(groups.stream().map(GroupDto::getId).collect(Collectors.toList()))
                                        .build());

        SchoolClassDto schoolClass = getSchoolClass(classes.get(0).getId());
        assertNotNull(schoolClass);
        assertEquals(3, schoolClass.getGroups().size());
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
    private void createClassToGroupRelations(ClassToGroupRelationDto relationDto) {
        mockMvc.perform(
            put("/classToGroups")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(relationDto)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @SneakyThrows
    private void saveOrUpdateSchoolClass(SchoolClassDto dto) {
        mockMvc.perform(
            put("/classes")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.getMapper().writeValueAsString(dto)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @SneakyThrows
    private SchoolClassDto getSchoolClass(Long id) {
        String contentAsString = mockMvc.perform(
            get("/classes/" + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, SchoolClassResponse.class).getClassDto();
    }

    @SneakyThrows
    private List<SchoolClassDto> getSchoolClassesByFilter(SchoolClassFilter filter) {
        String contentAsString = mockMvc.perform(
            get("/classes")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(filter))).andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, SchoolClassListResponse.class).getClasses();
    }

    @Test
    @SneakyThrows
    public void deleteGroupAndCheckRelation() {
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-1").level(1).build());
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-2").level(1).build());
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-3").level(1).build());
        saveOrUpdateSchoolClass(SchoolClassDto.builder().caption("305").build());

        List<GroupDto> groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(3, groups.size());
        List<SchoolClassDto> classes = getSchoolClassesByFilter(SchoolClassFilter
                                                                    .builder()
                                                                    .caption("305").build());
        assertEquals(1, classes.size());

        createClassToGroupRelations(ClassToGroupRelationDto
                                        .builder()
                                        .schoolClassId(classes.get(0).getId())
                                        .groupIds(groups.stream().map(GroupDto::getId).collect(Collectors.toList()))
                                        .build());

        classes = getSchoolClassesByFilter(SchoolClassFilter.builder().build());
        assertEquals(1, classes.size());
        assertEquals(3, classes.get(0).getGroups().size());
        deleteGroup(groups.get(1).getId());
        classes = getSchoolClassesByFilter(SchoolClassFilter.builder().build());
        assertEquals(1, classes.size());
        assertEquals(2, classes.get(0).getGroups().size());
    }

    @SneakyThrows
    private void deleteGroup(Long id) {
        this.mockMvc.perform(delete("/groups/" + id).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

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
}
