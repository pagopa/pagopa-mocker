package it.gov.pagopa.mocker.repository;

import it.gov.pagopa.mocker.entity.MockResourceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MockResourceRepository extends MongoRepository<MockResourceEntity, String> {

    @Transactional
    Optional<MockResourceEntity> findById(String id);

}
