package ua.pustovalov.sigmatesttask.school.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.GroupToSubjectRelation;

import java.util.List;

@Repository
public interface GroupToSubjectRelationRepository extends CrudRepository<GroupToSubjectRelation, Long> {

    List<GroupToSubjectRelation> findAllByGroupId(Long groupId);

    void deleteAllByGroupId(Long groupId);

    void deleteAllBySubjectId(Long subjectId);

}
