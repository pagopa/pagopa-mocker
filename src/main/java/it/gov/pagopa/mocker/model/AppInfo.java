package it.gov.pagopa.mocker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppInfo {

    private String name;
    private String version;
    private String environment;
    private String dbConnection;
    private String redisConnection;

    @Override
    public String toString() {
        return String.format("{ \"name\" : \"%s\", \"version\": \"%s\", \"environment\" : \"%s\", \"db_connection\" : \"%s\", \"redis_connection\" : \"%s\" }", name, version, environment, dbConnection, redisConnection);
    }
}
