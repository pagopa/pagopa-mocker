package it.gov.pagopa.mocker.entity.embeddable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InjectableParameterKey implements Serializable {

    @Column(name = "response_id", insertable = false, updatable = false)
    private String responseId;

    @Column(name = "parameter")
    private String parameter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InjectableParameterKey that = (InjectableParameterKey) o;
        return responseId.equals(that.responseId) && parameter.equals(that.parameter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseId, parameter);
    }
}
