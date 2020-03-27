package ua.pustovalov.sigmatesttask.school.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Builder
@Entity
@Table(name = "group_subject_relation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupToSubjectRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @NotNull
    private Group group;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @NotNull
    private Subject subject;

}
