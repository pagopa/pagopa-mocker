package it.gov.pagopa.mocker.entity;

import javax.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mock_response")
public class MockResponseEntity implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "body")
    private String body;

    @Column(name = "status")
    private int status;

    @OneToMany(targetEntity = InjectableParameterEntity.class, mappedBy = "response")
    private List<InjectableParameterEntity> parameters;

    @OneToMany(targetEntity = ResponseHeaderEntity.class, mappedBy = "response")
    private List<ResponseHeaderEntity> headers;

    @OneToOne(targetEntity = MockRuleEntity.class, fetch = FetchType.LAZY, optional = false, mappedBy = "response")
    private MockRuleEntity rule;
}
