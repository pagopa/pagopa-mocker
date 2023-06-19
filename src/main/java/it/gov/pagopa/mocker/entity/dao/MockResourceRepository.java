package it.gov.pagopa.mocker.entity.dao;

import it.gov.pagopa.mocker.entity.MockResourceEntity;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface MockResourceRepository extends Repository<MockResourceEntity, String> {

    @Transactional
    MockResourceEntity findById(String id);

}
