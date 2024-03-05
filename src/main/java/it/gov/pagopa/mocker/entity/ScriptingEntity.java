package it.gov.pagopa.mocker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ScriptingEntity implements Serializable {

    private String scriptName;

    private Boolean isActive;

    private List<NameValueEntity> parameters;
}
