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
import ua.pustovalov.sigmatesttask.school.controller.response.SubjectListResponse;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.dto.GroupToSubjectRelationDto;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.filter.GroupFilter;
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
public class GroupSubjectsRelationTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * This test create group and subjects, than create relation between and check that everything fine.
     */
    @Test
    @SneakyThrows
    public void createGroupsSubjectRelation() {
        GroupDto group = GroupDto.builder().caption("KNPZ-18-1").level(1).build();
        saveOrUpdateGroup(group);
        saveOrUpdateSubject(SubjectDto.builder().caption("math").build());
        saveOrUpdateSubject(SubjectDto.builder().caption("geography").defaultSubject(true).build());

        List<SubjectDto> subjects = getSubjectsByFilter(SubjectFilter.builder().build());

        List<GroupDto> groups = getGroupsByFilter(GroupFilter
                                                      .builder()
                                                      .caption(group.getCaption()).build()
        );
        assertEquals(1, groups.size());

        createGroupSubjectRelations(GroupToSubjectRelationDto
                                        .builder()
                                        .groupId(groups.get(0).getId())
                                        .subjectIds(subjects.stream().map(SubjectDto::getId).collect(Collectors.toList()))
                                        .build());
        GroupDto groupDto = getGroup(groups.get(0).getId());
        assertNotNull(groupDto);
        assertEquals(2, groupDto.getSubjects().size());
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
    private void createGroupSubjectRelations(GroupToSubjectRelationDto relationDto) {
        mockMvc.perform(
            put("/groupToSubjects")
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
    private List<SubjectDto> getSubjectsByFilter(SubjectFilter filter) {
        String contentAsString = mockMvc.perform(
            get("/subjects")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(filter)))
                                        .andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, SubjectListResponse.class).getSubjects();
    }

    @Test
    @SneakyThrows
    public void deleteSubjectAndCheckRelation() {
        GroupDto group = GroupDto.builder().caption("KNPZ-18-1").level(1).build();
        saveOrUpdateGroup(group);
        saveOrUpdateSubject(SubjectDto.builder().caption("math").build());
        saveOrUpdateSubject(SubjectDto.builder().caption("geography").defaultSubject(true).build());
        List<SubjectDto> subjects = getSubjectsByFilter(SubjectFilter.builder().build());
        assertEquals(2, subjects.size());
        List<GroupDto> groups = getGroupsByFilter(GroupFilter
                                                      .builder()
                                                      .caption(group.getCaption()).build());
        assertEquals(1, groups.size());
        createGroupSubjectRelations(GroupToSubjectRelationDto
                                        .builder()
                                        .groupId(groups.get(0).getId())
                                        .subjectIds(subjects.stream().map(SubjectDto::getId).collect(Collectors.toList()))
                                        .build());
        groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(1, groups.size());
        assertEquals(2, groups.get(0).getSubjects().size());
        deleteSubject(subjects.get(1).getId());
        groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(1, groups.size());
        assertEquals(1, groups.get(0).getSubjects().size());
    }

    @SneakyThrows
    private void deleteSubject(Long id) {
        this.mockMvc.perform(delete("/subjects/" + id).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

    }

}
