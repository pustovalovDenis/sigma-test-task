package ua.pustovalov.sigmatesttask.school.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.SubjectToStudentRelation;

import java.util.List;

@Repository
public interface SubjectToStudentRelationRepository extends CrudRepository<SubjectToStudentRelation, Long> {

    List<SubjectToStudentRelation> findAllByStudentId(Long studentId);

    void deleteAllBySubjectId(Long subjectId);

    void deleteAllByStudentId(Long studentId);

}
