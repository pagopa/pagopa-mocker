package it.gov.pagopa.mocker.entity;

import it.gov.pagopa.mocker.model.enumeration.ConditionType;
import it.gov.pagopa.mocker.model.enumeration.ContentType;
import it.gov.pagopa.mocker.model.enumeration.RuleFieldPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "mock_condition")
public class MockConditionEntity implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "\"order\"")
    private int order;

    @Column(name = "field_position")
    @Enumerated(EnumType.STRING)
    private RuleFieldPosition fieldPosition;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    private ContentType analyzedContentType;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "condition_type")
    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    @Column(name = "condition_value")
    private String conditionValue;

    @Column(name = "rule_id", insertable = false, updatable = false)
    private String ruleId;

    @ManyToOne(targetEntity = MockRuleEntity.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id")
    private MockRuleEntity rule;
}
