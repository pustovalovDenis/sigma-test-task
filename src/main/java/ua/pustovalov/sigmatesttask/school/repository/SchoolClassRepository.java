package ua.pustovalov.sigmatesttask.school.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.SchoolClass;
import ua.pustovalov.sigmatesttask.school.repository.query.SchoolClassQueryRepository;

@Repository
public interface SchoolClassRepository extends CrudRepository<SchoolClass, Long>, SchoolClassQueryRepository {
}
