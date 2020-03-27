package ua.pustovalov.sigmatesttask.school.repository.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.Subject;
import ua.pustovalov.sigmatesttask.school.filter.SubjectFilter;

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
public class SubjectQueryRepositoryImpl implements SubjectQueryRepository {

    protected final EntityManager entityManager;

    @Override
    public List<Subject> findAllByFilter(SubjectFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Subject> criteriaQuery = criteriaBuilder.createQuery(Subject.class);
        Root<Subject> root = criteriaQuery.from(Subject.class);
        List<Predicate> operationList = createPredicateOperationList(filter, criteriaBuilder, root);
        return entityManager.createQuery(
            criteriaQuery.where(criteriaBuilder.and(operationList.toArray(new Predicate[0])))
                         .orderBy(criteriaBuilder.asc(root.get(Subject.Fields.id)))
        ).getResultList();
    }

    private List<Predicate> createPredicateOperationList(SubjectFilter filter, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> operationList = new ArrayList<>();

        Optional.ofNullable(filter.getId())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Subject.Fields.id), val)));

        Optional.ofNullable(filter.getCaption())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Subject.Fields.caption), val)));

        Optional.ofNullable(filter.getDefaultSubject())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Subject.Fields.defaultSubject), val)));

        return operationList;
    }
}
