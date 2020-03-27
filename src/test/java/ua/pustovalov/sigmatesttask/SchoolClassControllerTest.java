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
import ua.pustovalov.sigmatesttask.school.controller.response.SchoolClassListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.SchoolClassResponse;
import ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto;
import ua.pustovalov.sigmatesttask.school.filter.SchoolClassFilter;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SchoolClassControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void saveOrUpdateSchoolClassTest() {
        saveOrUpdateSchoolClass(SchoolClassDto.builder().caption("302").build());
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

    @Test
    @SneakyThrows
    public void checkFindAllTest() {
        saveOrUpdateSchoolClass(SchoolClassDto.builder().caption("305").build());
        saveOrUpdateSchoolClass(SchoolClassDto.builder().caption("306").build());
        String contentAsString = mockMvc.perform(
            get("/classes")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(SchoolClassFilter
                                                                 .builder()
                                                                 .build()
                ))).andReturn().getResponse().getContentAsString();
        List<SchoolClassDto> classes = this.getMapper().readValue(contentAsString, SchoolClassListResponse.class).getClasses();
        assertEquals(2, classes.size());
    }

    @Test
    @SneakyThrows
    public void checkSchoolClassFilter() {
        saveOrUpdateSchoolClass(SchoolClassDto.builder().caption("305").build());
        saveOrUpdateSchoolClass(SchoolClassDto.builder().caption("306").build());
        List<SchoolClassDto> classes = getSchoolClassesByFilter(SchoolClassFilter.builder().caption("305").build());
        assertEquals(1, classes.size());
        classes = getSchoolClassesByFilter(SchoolClassFilter.builder().caption("306").build());
        assertEquals(1, classes.size());
        classes = getSchoolClassesByFilter(SchoolClassFilter.builder().build());
        assertEquals(2, classes.size());
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
    public void checkFindByIdTest() {
        SchoolClassDto expected = SchoolClassDto.builder().caption("304")
                                                .levels(Arrays.asList(1, 2, 3, 4, 5, 6))
                                                .build();
        saveOrUpdateSchoolClass(expected);
        List<SchoolClassDto> classes = getSchoolClassesByFilter(SchoolClassFilter.builder().caption("304").build());
        assertEquals(1, classes.size());
        SchoolClassDto actual = getSchoolClass(classes.get(0).getId());
        assertEquals(expected.getCaption(), actual.getCaption());
        assertEquals(expected.getCaption(), actual.getCaption());
        assertEquals(expected.getLevels(), actual.getLevels());
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

    @Test
    @SneakyThrows
    public void checkDeleteByIdTest() {
        SchoolClassDto expected = SchoolClassDto.builder().caption("301").build();
        saveOrUpdateSchoolClass(expected);
        List<SchoolClassDto> classes = getSchoolClassesByFilter(SchoolClassFilter.builder().build());
        assertEquals(1, classes.size());
        deleteSchoolClass(classes.get(0).getId());
        classes = getSchoolClassesByFilter(SchoolClassFilter.builder().build());
        assertEquals(0, classes.size());
    }

    @SneakyThrows
    private void deleteSchoolClass(Long id) {
        this.mockMvc.perform(delete("/classes/" + id).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

    }

    @Test
    @SneakyThrows
    public void checkDeleteWithWrongIdTest() {
        this.mockMvc.perform(delete("/classes/" + 111L).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
    }

}
