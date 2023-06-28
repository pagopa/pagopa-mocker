package it.gov.pagopa.mocker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "mock_rule")
public class MockRuleEntity implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "\"order\"")
    private int order;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "resource_id", insertable = false, updatable = false)
    private String resourceId;

    @Column(name = "response_id", insertable = false, updatable = false)
    private String responseId;

    @OneToMany(targetEntity = MockConditionEntity.class, fetch = FetchType.EAGER, mappedBy = "rule")
    private List<MockConditionEntity> conditions;

    @OneToOne(targetEntity = MockResponseEntity.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "response_id")
    private MockResponseEntity response;

    @ManyToOne(targetEntity = MockResourceEntity.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id")
    private MockResourceEntity resource;
}
