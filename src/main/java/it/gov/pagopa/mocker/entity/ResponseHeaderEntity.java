package it.gov.pagopa.mocker.entity;

import it.gov.pagopa.mocker.entity.embeddable.ResponseHeaderKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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
