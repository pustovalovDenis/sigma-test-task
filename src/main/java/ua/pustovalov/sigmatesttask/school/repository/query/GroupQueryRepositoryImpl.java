package ua.pustovalov.sigmatesttask.school.repository.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ua.pustovalov.sigmatesttask.school.entity.Group;
import ua.pustovalov.sigmatesttask.school.filter.GroupFilter;

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
public class GroupQueryRepositoryImpl implements GroupQueryRepository {

    protected final EntityManager entityManager;

    @Override
    public List<Group> findAllByFilter(GroupFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Group> criteriaQuery = criteriaBuilder.createQuery(Group.class);
        Root<Group> root = criteriaQuery.from(Group.class);
        List<Predicate> operationList = createPredicateOperationList(filter, criteriaBuilder, root);
        return entityManager.createQuery(
            criteriaQuery.where(criteriaBuilder.and(operationList.toArray(new Predicate[0])))
                         .orderBy(criteriaBuilder.asc(root.get(Group.Fields.id)))
        ).getResultList();
    }

    private List<Predicate> createPredicateOperationList(GroupFilter filter, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> operationList = new ArrayList<>();

        Optional.ofNullable(filter.getId())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Group.Fields.id), val)));

        Optional.ofNullable(filter.getCaption())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Group.Fields.caption), val)));

        Optional.ofNullable(filter.getLevel())
                .ifPresent(val -> operationList.add(criteriaBuilder.equal(root.get(Group.Fields.level), val)));

        return operationList;
    }
}
