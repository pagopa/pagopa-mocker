package it.gov.pagopa.mocker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MockRuleEntity implements Serializable {

    private String id;

    private String name;

    private int order;

    private boolean isActive;

    private List<MockConditionEntity> conditions;

    private MockResponseEntity response;

    private ScriptingEntity scripting;
}
