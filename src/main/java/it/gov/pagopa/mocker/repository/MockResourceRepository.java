package it.gov.pagopa.mocker.repository;

import it.gov.pagopa.mocker.entity.MockResourceEntity;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MockResourceRepository extends Repository<MockResourceEntity, String> {

    @Transactional
    Optional<MockResourceEntity> findById(String id);

}
