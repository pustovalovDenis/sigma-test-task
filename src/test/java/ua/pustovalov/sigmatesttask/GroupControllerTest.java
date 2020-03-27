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
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.filter.GroupFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void saveOrUpdateGroupTest() {
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-1").level(1).build());
    }

    @SneakyThrows
    private void saveOrUpdateGroup(GroupDto dto) {
        mockMvc.perform(
            put("/groups")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.getMapper().writeValueAsString(dto)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void checkFindAllTest() {
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-1").level(1).build());
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-2").level(1).build());
        String contentAsString = mockMvc.perform(
            get("/groups")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(GroupFilter
                                                                 .builder()
                                                                 .build()
                ))).andReturn().getResponse().getContentAsString();
        List<GroupDto> groups = this.getMapper().readValue(contentAsString, GroupListResponse.class).getGroups();
        assertEquals(2, groups.size());
    }

    @Test
    @SneakyThrows
    public void checkGroupFilter() {
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-1").level(1).build());
        saveOrUpdateGroup(GroupDto.builder().caption("KNPZ-18-2").level(1).build());
        List<GroupDto> groups = getGroupsByFilter(GroupFilter.builder().caption("KNPZ-18-1").build());
        assertEquals(1, groups.size());
        groups = getGroupsByFilter(GroupFilter.builder().caption("KNPZ-18-2").build());
        assertEquals(1, groups.size());
        groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(2, groups.size());
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
    public void checkFindByIdTest() {
        GroupDto expected = GroupDto.builder().caption("KNPZ-18-1")
                                    .maxCapacity(10)
                                    .minCapacity(2)
                                    .level(1)
                                    .build();
        saveOrUpdateGroup(expected);
        List<GroupDto> groups = getGroupsByFilter(GroupFilter.builder().caption("KNPZ-18-1").build());
        assertEquals(1, groups.size());
        GroupDto actual = getGroup(groups.get(0).getId());
        assertEquals(expected.getCaption(), actual.getCaption());
        assertEquals(expected.getMaxCapacity(), actual.getMaxCapacity());
        assertEquals(expected.getMinCapacity(), actual.getMinCapacity());
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

    @Test
    @SneakyThrows
    public void checkDeleteByIdTest() {
        GroupDto expected = GroupDto.builder().caption("KNPZ-18-1").level(1).build();
        saveOrUpdateGroup(expected);
        List<GroupDto> groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(1, groups.size());
        deleteGroup(groups.get(0).getId());
        groups = getGroupsByFilter(GroupFilter.builder().build());
        assertEquals(0, groups.size());
    }

    @SneakyThrows
    private void deleteGroup(Long id) {
        this.mockMvc.perform(delete("/groups/" + id).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

    }

    @Test
    @SneakyThrows
    public void checkDeleteWithWrongIdTest() {
        this.mockMvc.perform(delete("/groups/" + 111L).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
    }

}
