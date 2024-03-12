package it.gov.pagopa.mocker.entity;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("scripts")
@ToString
public class ScriptEntity implements Serializable {

    @Id
    private String id;

    private String name;

    private Boolean selectable;

    private String code;
}
