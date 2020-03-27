package ua.pustovalov.sigmatesttask.school.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.Student;
import ua.pustovalov.sigmatesttask.school.repository.query.StudentQueryRepository;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long>, StudentQueryRepository {
}
