package ua.pustovalov.sigmatesttask.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;
import ua.pustovalov.sigmatesttask.school.entity.Student;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    StudentDto convert(Student student);

    @Mapping(target = "id", ignore = true)
    Student convert(StudentDto student);

    List<StudentDto> convert(List<Student> studentList);

}
