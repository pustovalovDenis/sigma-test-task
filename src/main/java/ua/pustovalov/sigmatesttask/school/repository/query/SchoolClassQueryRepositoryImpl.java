package ua.pustovalov.sigmatesttask.school.repository.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.SchoolClass;
import ua.pustovalov.sigmatesttask.school.filter.SchoolClassFilter;

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
public class SchoolClassQueryRepositoryImpl implements SchoolClassQueryRepository {

    protected final EntityManager entityManager;

    @Override
    public List<SchoolClass> findAllByFilter(SchoolClassFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SchoolClass> criteriaQuery = criteriaBuilder.createQuery(SchoolClass.class);
        Root<SchoolClass> root = criteriaQuery.from(SchoolClass.class);
        List<Predicate> operationList = createPredicateOperationList(filter, criteriaBuilder, root);
        return entityManager.createQuery(
            criteriaQuery.where(criteriaBuilder.and(operationList.toArray(new Predicate[0])))
                         .orderBy(criteriaBuilder.asc(root.get(SchoolClass.Fields.id)))
        ).getResultList();
    }

    private List<Predicate> createPredicateOperationList(SchoolClassFilter filter, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> operationList = new ArrayList<>();

        Optional.ofNullable(filter.getId())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(SchoolClass.Fields.id), val)));

        Optional.ofNullable(filter.getCaption())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(SchoolClass.Fields.caption), val)));

        return operationList;
    }

}
