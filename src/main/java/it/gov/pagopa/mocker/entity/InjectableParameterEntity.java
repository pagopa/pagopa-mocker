package it.gov.pagopa.mocker.entity;

import it.gov.pagopa.mocker.entity.embeddable.InjectableParameterKey;
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
@Table(name = "injectable_parameter")
public class InjectableParameterEntity implements Serializable {

    @EmbeddedId
    private InjectableParameterKey id;

     @ManyToOne(targetEntity = MockResponseEntity.class, fetch = FetchType.LAZY, optional = false)
     @JoinColumn(name = "response_id", insertable = false, updatable = false)
     private MockResponseEntity response;
}
