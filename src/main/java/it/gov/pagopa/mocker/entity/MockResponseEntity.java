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
public class MockResponseEntity implements Serializable {

    private String body;

    private int status;

    private List<String> parameters;

    private List<ResponseHeaderEntity> headers;
}
