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

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "name")
    private String name;

    @OneToMany(targetEntity = MockRuleEntity.class, fetch = FetchType.EAGER, mappedBy = "resource")
    private List<MockRuleEntity> rules;

    @ManyToMany(targetEntity = TagEntity.class)
    @JoinTable(name = "mock_resource_tag",
            joinColumns = {@JoinColumn(name = "mock_resource_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")})
    private List<TagEntity> tags;
}
