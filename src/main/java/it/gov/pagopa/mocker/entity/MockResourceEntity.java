package it.gov.pagopa.mocker.entity;

import it.gov.pagopa.mocker.model.enumeration.HttpMethod;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("mock_resources")
@ToString
public class MockResourceEntity implements Serializable {

    @Id
    private String id;

    private String subsystemUrl;

    private String resourceUrl;

    private HttpMethod httpMethod;

    private List<NameValueEntity> specialHeaders;

    private Boolean isActive;

    private String name;

    private List<MockRuleEntity> rules;
}
