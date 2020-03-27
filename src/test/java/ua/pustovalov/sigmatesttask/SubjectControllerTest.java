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
import ua.pustovalov.sigmatesttask.school.controller.response.SubjectListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.SubjectResponse;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.filter.SubjectFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubjectControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void saveOrUpdateSubjectTest() {
        saveOrUpdateSubject(SubjectDto.builder().caption("math").build());
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

    @Test
    @SneakyThrows
    public void checkFindAllTest() {
        saveOrUpdateSubject(SubjectDto.builder().caption("math").build());
        saveOrUpdateSubject(SubjectDto.builder().caption("geography").build());
        String contentAsString = mockMvc.perform(
            get("/subjects")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(SubjectFilter
                                                                 .builder()
                                                                 .build()
                ))).andReturn().getResponse().getContentAsString();
        List<SubjectDto> subjects = this.getMapper().readValue(contentAsString, SubjectListResponse.class).getSubjects();
        assertEquals(2, subjects.size());
    }

    @Test
    @SneakyThrows
    public void checkSubjectFilter() {
        saveOrUpdateSubject(SubjectDto.builder().caption("math").build());
        saveOrUpdateSubject(SubjectDto.builder().caption("geography").build());
        List<SubjectDto> subjects = getSubjectsByFilter(SubjectFilter.builder().caption("geography").build());
        assertEquals(1, subjects.size());
        subjects = getSubjectsByFilter(SubjectFilter.builder().caption("math").build());
        assertEquals(1, subjects.size());
        subjects = getSubjectsByFilter(SubjectFilter.builder().build());
        assertEquals(2, subjects.size());
    }

    @SneakyThrows
    private List<SubjectDto> getSubjectsByFilter(SubjectFilter filter) {
        String contentAsString = mockMvc.perform(
            get("/subjects")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(filter))).andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, SubjectListResponse.class).getSubjects();
    }

    @Test
    @SneakyThrows
    public void checkFindByIdTest() {
        SubjectDto expected = SubjectDto.builder().caption("geography").defaultSubject(true).build();
        saveOrUpdateSubject(expected);
        List<SubjectDto> subjects = getSubjectsByFilter(SubjectFilter.builder().caption("geography").defaultSubject(true).build());
        assertEquals(1, subjects.size());
        SubjectDto actual = getSubject(subjects.get(0).getId());
        assertEquals(expected.getCaption(), actual.getCaption());
        assertEquals(expected.getDefaultSubject(), actual.getDefaultSubject());
    }

    @SneakyThrows
    private SubjectDto getSubject(Long id) {
        String contentAsString = mockMvc.perform(
            get("/subjects/" + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, SubjectResponse.class).getSubject();
    }

    @Test
    @SneakyThrows
    public void checkDeleteByIdTest() {
        SubjectDto expected = SubjectDto.builder().caption("geography").defaultSubject(true).build();
        saveOrUpdateSubject(expected);
        List<SubjectDto> subjects = getSubjectsByFilter(SubjectFilter.builder().build());
        assertEquals(1, subjects.size());
        deleteSubject(subjects.get(0).getId());
        subjects = getSubjectsByFilter(SubjectFilter.builder().build());
        assertEquals(0, subjects.size());
    }

    @SneakyThrows
    private void deleteSubject(Long id) {
        this.mockMvc.perform(delete("/subjects/" + id).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

    }

    @Test
    @SneakyThrows
    public void checkDeleteWithWrongIdTest() {
        this.mockMvc.perform(delete("/subjects/" + 111L).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
    }

}
