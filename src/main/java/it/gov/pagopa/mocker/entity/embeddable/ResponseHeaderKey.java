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
public class ResponseHeaderKey implements Serializable {

    @Column(name = "response_id", insertable = false, updatable = false)
    private String responseId;

    @Column(name = "header")
    private String header;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseHeaderKey that = (ResponseHeaderKey) o;
        return responseId.equals(that.responseId) && header.equals(that.header);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseId, header);
    }
}
