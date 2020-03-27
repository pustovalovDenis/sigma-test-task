package ua.pustovalov.sigmatesttask.school.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.ClassToGroupRelation;

import java.util.List;

@Repository
public interface ClassToGroupRelationRepository extends CrudRepository<ClassToGroupRelation, Long> {

    List<ClassToGroupRelation> findAllBySchoolClassId(Long schoolClassId);

    void deleteAllByGroupId(Long groupId);

    void deleteAllBySchoolClassId(Long schoolClassId);

}
