package it.gov.pagopa.mocker.repository;

import it.gov.pagopa.mocker.entity.ScriptEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptRepository extends MongoRepository<ScriptEntity, String> {

}
