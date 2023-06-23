package it.gov.pagopa.mocker.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tag")
public class TagEntity implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "value")
    private String value;

    @ManyToMany(targetEntity = MockResourceEntity.class)
    //@JoinTable(name = "mock_resource_tag", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
    @JoinTable(name = "mock_resource_tag",
            joinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "mock_resource_id", referencedColumnName = "id")})
    private List<MockResourceEntity> resources;
}
