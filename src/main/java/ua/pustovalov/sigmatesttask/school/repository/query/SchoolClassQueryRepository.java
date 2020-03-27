package ua.pustovalov.sigmatesttask.school.repository.query;

import ua.pustovalov.sigmatesttask.school.entity.SchoolClass;
import ua.pustovalov.sigmatesttask.school.filter.SchoolClassFilter;

import java.util.List;

public interface SchoolClassQueryRepository {

    List<SchoolClass> findAllByFilter(SchoolClassFilter filter);

}
