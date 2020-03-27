package ua.pustovalov.sigmatesttask.school.repository.query;

import ua.pustovalov.sigmatesttask.school.entity.Group;
import ua.pustovalov.sigmatesttask.school.filter.GroupFilter;

import java.util.List;

public interface GroupQueryRepository {

    List<Group> findAllByFilter(GroupFilter filter);

}
