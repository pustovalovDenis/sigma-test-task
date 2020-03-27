package ua.pustovalov.sigmatesttask.school.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.StudentToGroupRelation;

import java.util.List;

@Repository
public interface StudentToGroupRelationRepository extends CrudRepository<StudentToGroupRelation, Long> {

    List<StudentToGroupRelation> findAllByGroupId(Long groupId);

    void deleteAllByGroupId(Long groupId);

    void deleteAllByStudentId(Long studentId);

}
