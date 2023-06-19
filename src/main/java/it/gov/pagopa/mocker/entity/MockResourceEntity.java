package it.gov.pagopa.mocker.entity;

import it.gov.pagopa.mocker.model.enumeration.HttpMethod;
import javax.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "http_method")
    @Enumerated(EnumType.STRING)
    private HttpMethod httpMethod;

    @Column(name = "name")
    private String name;

    @Column(name = "tags")
    private String tags;

    @OneToMany(targetEntity = MockRuleEntity.class, fetch = FetchType.EAGER, mappedBy = "resource")
    private List<MockRuleEntity> rules;

}
