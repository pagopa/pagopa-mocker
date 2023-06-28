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
@Table(name = "mock_response")
public class MockResponseEntity implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "body")
    private String body;

    @Column(name = "status")
    private int status;

    @OneToMany(targetEntity = InjectableParameterEntity.class, mappedBy = "response", cascade = CascadeType.ALL)
    private List<InjectableParameterEntity> parameters;

    @OneToMany(targetEntity = ResponseHeaderEntity.class, mappedBy = "response", cascade = CascadeType.ALL)
    private List<ResponseHeaderEntity> headers;

    @OneToOne(targetEntity = MockRuleEntity.class, fetch = FetchType.LAZY, optional = false, mappedBy = "response", cascade = CascadeType.ALL)
    private MockRuleEntity rule;
}
