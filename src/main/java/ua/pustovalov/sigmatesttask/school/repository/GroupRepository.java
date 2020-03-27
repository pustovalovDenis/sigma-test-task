package ua.pustovalov.sigmatesttask.school.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.Group;
import ua.pustovalov.sigmatesttask.school.repository.query.GroupQueryRepository;

@Repository
public interface GroupRepository extends CrudRepository<Group, Long>, GroupQueryRepository {
}
