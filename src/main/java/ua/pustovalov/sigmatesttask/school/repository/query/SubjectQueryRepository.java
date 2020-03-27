package ua.pustovalov.sigmatesttask.school.repository.query;

import ua.pustovalov.sigmatesttask.school.entity.Subject;
import ua.pustovalov.sigmatesttask.school.filter.SubjectFilter;

import java.util.List;

public interface SubjectQueryRepository {

    List<Subject> findAllByFilter(SubjectFilter filter);

}
