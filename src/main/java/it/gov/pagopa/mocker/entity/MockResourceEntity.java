package it.gov.pagopa.mocker.entity;

import it.gov.pagopa.mocker.model.enumeration.HttpMethod;
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
@Table(name = "mock_resource")
public class MockResourceEntity implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "subsystem_url")
    private String subsystemUrl;

    @Column(name = "resource_url")
    private String resourceUrl;

    @Column(name = "action")
    private String action;

    @Column(name = "http_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod httpMethod;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "name")
    private String name;

    @OneToMany(targetEntity = MockRuleEntity.class, fetch = FetchType.EAGER, mappedBy = "resource", cascade = CascadeType.ALL)
    private List<MockRuleEntity> rules;
}
