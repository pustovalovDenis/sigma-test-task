package ua.pustovalov.sigmatesttask;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;
import ua.pustovalov.sigmatesttask.school.controller.response.GroupListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.SchoolClassListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.StudentListResponse;
import ua.pustovalov.sigmatesttask.school.controller.response.SubjectListResponse;
import ua.pustovalov.sigmatesttask.school.dto.GroupDto;
import ua.pustovalov.sigmatesttask.school.dto.GroupToSubjectRelationDto;
import ua.pustovalov.sigmatesttask.school.dto.SchoolClassDto;
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;
import ua.pustovalov.sigmatesttask.school.dto.SubjectDto;
import ua.pustovalov.sigmatesttask.school.dto.SubjectToStudentRelationDto;
import ua.pustovalov.sigmatesttask.school.filter.GroupFilter;
import ua.pustovalov.sigmatesttask.school.filter.SchoolClassFilter;
import ua.pustovalov.sigmatesttask.school.filter.StudentFilter;
import ua.pustovalov.sigmatesttask.school.filter.SubjectFilter;
import ua.pustovalov.sigmatesttask.school.service.SchoolClassesToGroupAssignorProcessor;
import ua.pustovalov.sigmatesttask.school.service.StudentToGroupAssignorProcessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Getter
public class SchoolTest extends AbstractTest {

    private final static int MAX_COUNT_SUBJECT_IN_GROUP = 3;

    @Autowired
    private MockMvc mockMvc;

    private List<StudentDto> students;

    private List<SubjectDto> subjects;

    private List<GroupDto> groups;

    private List<SchoolClassDto> classes;

    @Autowired
    private StudentToGroupAssignorProcessor studentToGroupAssignorProcessor;

    @Autowired
    private SchoolClassesToGroupAssignorProcessor schoolClassesToGroupAssignorProcessor;

    @Override
    @SneakyThrows
    @Before
    public void init() {
        super.init();
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("student/students.json")))
        )) {
            String jsonString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            students = this.getMapper().readValue(jsonString, new TypeReference<List<StudentDto>>() {});
        }

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("subject/subjects.json")))
        )) {
            String jsonString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            subjects = this.getMapper().readValue(jsonString, new TypeReference<List<SubjectDto>>() {});
        }
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("groups/groups.json")))
        )) {
            String jsonString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            groups = this.getMapper().readValue(jsonString, new TypeReference<List<GroupDto>>() {});
        }

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("classes/classes.json")))
        )) {
            String jsonString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            classes = this.getMapper().readValue(jsonString, new TypeReference<List<SchoolClassDto>>() {});
        }
    }

    @Test
    @SneakyThrows
    public void checkSchoolStudentsAssignProcessor() {
        List<StudentDto> studentDtos = prepareStudents();
        List<GroupDto> groupDtos = prepareGroups();
        List<SubjectDto> subjectDtos = prepareSubjects();
        List<SchoolClassDto> classes = prepareSchoolClasses();
        classes.forEach(val -> Assert.notEmpty(val.getLevels(), "Level collection must contain elements"));
        for (StudentDto studentDto : studentDtos) {
            List<SubjectDto> assignedSubjects = prepareAssignedSubjects(subjectDtos);
            createStudentSubjectRelations(SubjectToStudentRelationDto
                                              .builder()
                                              .studentId(studentDto.getId())
                                              .subjectIds(assignedSubjects.stream().map(SubjectDto::getId).collect(Collectors.toList()))
                                              .build());
        }
        List<StudentDto> studentsWithSubjects = getStudentsByFilter(StudentFilter.builder().build());
        studentsWithSubjects.forEach(val -> Assert.notEmpty(val.getSubjects(), "Collection must contain elements"));

        for (GroupDto groupDto : groupDtos) {
            List<SubjectDto> assignedSubjects = prepareAssignedSubjectsToGroup(subjectDtos);
            createGroupSubjectRelations(GroupToSubjectRelationDto
                                            .builder()
                                            .groupId(groupDto.getId())
                                            .subjectIds(assignedSubjects.stream().map(SubjectDto::getId).collect(Collectors.toList()))
                                            .build());
        }
        List<GroupDto> groupsWithSubjects = getGroupsByFilter(GroupFilter.builder().build());
        groupsWithSubjects.forEach(val -> Assert.notEmpty(val.getSubjects(), "Collection must contain elements"));

        studentToGroupAssignorProcessor.assignStudentsToGroup(studentsWithSubjects, groupsWithSubjects);
        List<GroupDto> groupsWithSubjectsAndStudents = getGroupsByFilter(GroupFilter.builder().build());

        // Check that all groups contains right students
        groupsWithSubjectsAndStudents.forEach(group -> group.getStudents().stream().filter(student -> student.getLevel().equals(group.getLevel())).forEach(student -> {
            List<String> groupSubjectsCaption = group.getSubjects().stream().map(SubjectDto::getCaption).collect(Collectors.toList());
            List<String> studentSubjectsCaption = student.getSubjects().stream().map(SubjectDto::getCaption).collect(Collectors.toList());
            assertTrue(group.getMaxCapacity() >= group.getStudents().size());
            assertTrue(studentSubjectsCaption.containsAll(groupSubjectsCaption));
        }));
        schoolClassesToGroupAssignorProcessor.assignGroupsToClasses(classes, groupsWithSubjectsAndStudents);
        List<SchoolClassDto> schoolClassesWithAssignedGroups = getSchoolClassesByFilter(SchoolClassFilter.builder().build());
        //This class should be always with groups
        SchoolClassDto classDto = schoolClassesWithAssignedGroups.get(0);
        assertNotEquals(0, classDto.getGroups().size());
    }

    private List<SubjectDto> prepareAssignedSubjectsToGroup(List<SubjectDto> subjectDtos) {
        Set<SubjectDto> result = new HashSet<>();
        for (int i = 0; i < MAX_COUNT_SUBJECT_IN_GROUP; i++) {
            result.add(subjectDtos.get((int) (Math.random() * MAX_COUNT_SUBJECT_IN_GROUP)));
        }
        return new ArrayList<>(result);
    }

    private List<SubjectDto> prepareAssignedSubjects(List<SubjectDto> subjectDtos) {
        List<SubjectDto> mandatorySubjects = subjectDtos.stream().filter(SubjectDto::getDefaultSubject).collect(Collectors.toList());
        List<SubjectDto> notMandatorySubjects = subjectDtos.stream().filter(val -> !val.getDefaultSubject()).collect(Collectors.toList());
        int countOfNotMandatorySubjects = notMandatorySubjects.size();
        int randomSubject = (int) (Math.random() * countOfNotMandatorySubjects);
        List<SubjectDto> result = new ArrayList<>(mandatorySubjects);
        result.add(notMandatorySubjects.get(randomSubject));
        return result;
    }

    private List<StudentDto> prepareStudents() {
        getStudents().forEach(this::prepareStudent);
        return getStudentsByFilter(StudentFilter.builder().build());
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

    @SneakyThrows
    private List<StudentDto> getStudentsByFilter(StudentFilter filter) {
        String contentAsString = mockMvc.perform(
            get("/students")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(filter))).andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, StudentListResponse.class).getStudents();
    }

    private List<GroupDto> prepareGroups() {
        getGroups().forEach(this::saveOrUpdateGroup);
        return getGroupsByFilter(GroupFilter.builder().build());
    }

    @SneakyThrows
    private void saveOrUpdateGroup(GroupDto subject) {
        mockMvc.perform(
            put("/groups")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.getMapper().writeValueAsString(subject)))
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

    private List<SubjectDto> prepareSubjects() {
        getSubjects().forEach(this::saveOrUpdateSubject);
        return getSubjectsByFilter(SubjectFilter.builder().build());
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
    private List<SubjectDto> getSubjectsByFilter(SubjectFilter filter) {
        String contentAsString = mockMvc.perform(
            get("/subjects")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(filter))).andReturn().getResponse().getContentAsString();
        return this.getMapper().readValue(contentAsString, SubjectListResponse.class).getSubjects();
    }

    private List<SchoolClassDto> prepareSchoolClasses() {
        getClasses().forEach(this::saveOrUpdateSchoolClass);
        return getSchoolClassesByFilter(SchoolClassFilter.builder().build());
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
    private void createGroupSubjectRelations(GroupToSubjectRelationDto relationDto) {
        mockMvc.perform(
            put("/groupToSubjects")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.getMapper().writeValueAsString(relationDto)))
               .andDo(print())
               .andExpect(status().isOk());
    }

}
