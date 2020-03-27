package ua.pustovalov.sigmatesttask.school.repository.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.Student;
import ua.pustovalov.sigmatesttask.school.filter.StudentFilter;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudentQueryRepositoryImpl implements StudentQueryRepository {

    protected final EntityManager entityManager;

    @Override
    public List<Student> findAllByFilter(StudentFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
        Root<Student> root = criteriaQuery.from(Student.class);
        List<Predicate> operationList = createPredicateOperationList(filter, criteriaBuilder, root);
        return entityManager.createQuery(
            criteriaQuery.where(criteriaBuilder.and(operationList.toArray(new Predicate[0])))
                         .orderBy(criteriaBuilder.asc(root.get(Student.Fields.id)))
        ).getResultList();
    }

    private List<Predicate> createPredicateOperationList(StudentFilter filter, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> operationList = new ArrayList<>();

        Optional.ofNullable(filter.getId())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Student.Fields.id), val)));

        Optional.ofNullable(filter.getName())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Student.Fields.name), val)));

        Optional.ofNullable(filter.getSurname())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Student.Fields.surname), val)));

        Optional.ofNullable(filter.getUnplaced())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Student.Fields.unplaced), val)));

        Optional.ofNullable(filter.getLevel())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Student.Fields.level), val)));

        return operationList;
    }
}
