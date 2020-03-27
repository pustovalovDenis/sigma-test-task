package ua.pustovalov.sigmatesttask.school.repository.query;

import ua.pustovalov.sigmatesttask.school.entity.Student;
import ua.pustovalov.sigmatesttask.school.filter.StudentFilter;

import java.util.List;

public interface StudentQueryRepository {

    List<Student> findAllByFilter(StudentFilter filter);

}
