package ua.pustovalov.sigmatesttask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import ua.pustovalov.sigmatesttask.school.repository.ClassToGroupRelationRepository;
import ua.pustovalov.sigmatesttask.school.repository.GroupRepository;
import ua.pustovalov.sigmatesttask.school.repository.GroupToSubjectRelationRepository;
import ua.pustovalov.sigmatesttask.school.repository.SchoolClassRepository;
import ua.pustovalov.sigmatesttask.school.repository.StudentRepository;
import ua.pustovalov.sigmatesttask.school.repository.StudentToGroupRelationRepository;
import ua.pustovalov.sigmatesttask.school.repository.SubjectRepository;
import ua.pustovalov.sigmatesttask.school.repository.SubjectToStudentRelationRepository;

@Getter
public abstract class AbstractTest {

    private ObjectMapper mapper;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private ClassToGroupRelationRepository classToGroupRelationRepository;

    @Autowired
    private GroupToSubjectRelationRepository groupToSubjectRelationRepository;

    @Autowired
    private StudentToGroupRelationRepository studentToGroupRelationRepository;

    @Autowired
    private SubjectToStudentRelationRepository subjectToStudentRelationRepository;

    @Before
    public void init() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    @After
    public void cleanup() {
        subjectToStudentRelationRepository.deleteAll();
        studentToGroupRelationRepository.deleteAll();
        groupToSubjectRelationRepository.deleteAll();
        classToGroupRelationRepository.deleteAll();
        groupRepository.deleteAll();
        subjectRepository.deleteAll();
        studentRepository.deleteAll();
        schoolClassRepository.deleteAll();
    }

}
