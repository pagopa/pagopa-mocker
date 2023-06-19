package it.gov.pagopa.mocker.entity;

import it.gov.pagopa.mocker.entity.embeddable.ResponseHeaderKey;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "response_header")
public class ResponseHeaderEntity implements Serializable {

    @EmbeddedId
    private ResponseHeaderKey id;

    @Column(name = "value")
    private String value;

    @ManyToOne(targetEntity = MockResponseEntity.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "response_id", insertable = false, updatable = false)
    private MockResponseEntity response;
}
