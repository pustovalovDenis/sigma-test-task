package ua.pustovalov.sigmatesttask.school.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import ua.pustovalov.sigmatesttask.school.repository.converter.IntegerListAttributeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Builder
@Entity
@Table(name = "school_class")
@Data
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String caption;

    @Convert(converter = IntegerListAttributeConverter.class)
    private List<Integer> levels;

}
