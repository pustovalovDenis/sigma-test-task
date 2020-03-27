package ua.pustovalov.sigmatesttask.school.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.Subject;
import ua.pustovalov.sigmatesttask.school.repository.query.SubjectQueryRepository;

@Repository
public interface SubjectRepository extends CrudRepository<Subject, Long>, SubjectQueryRepository {
}
