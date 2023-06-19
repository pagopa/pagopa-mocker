package it.gov.pagopa.mocker.service;

import it.gov.pagopa.mocker.entity.MockResourceEntity;
import it.gov.pagopa.mocker.entity.dao.MockResourceRepository;
import it.gov.pagopa.mocker.exceptions.*;
import it.gov.pagopa.mocker.model.ExtractedRequest;
import it.gov.pagopa.mocker.model.ExtractedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MockerService {

    @Autowired
    private MockResourceRepository dao;

    @Autowired
    private ResourceExtractor resourceExtractor;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExtractedResponse analyze(ExtractedRequest requestData) {
        ExtractedResponse response;
        try {
            long start = System.nanoTime();
            /* Retrieving mock resource from database in order to be evaluated */
            MockResourceEntity mockResource = getMockResourceFromDB(requestData);
            log.debug(String.format("The retrieving of mock response from database ended in [%s] ms", (System.nanoTime() - start) / 1000000 ));
            /* Extracting the main information for the response of the rule on which the request is compliant. */
            response = resourceExtractor.extract(requestData, mockResource);
        } catch (MockerException e) {
            response = ResponseBuilder.buildErrorResponse(e.getErrorMessage(), requestData);
        } catch (Exception e) {
            e.printStackTrace();
            response = ResponseBuilder.buildErrorResponse("An unhandled error occurred while searching for mocked resource.", requestData);
        }
        return response;
    }

    private MockResourceEntity getMockResourceFromDB(ExtractedRequest requestData) throws MockerNotRegisteredException {
        MockResourceEntity mockResource = dao.findById(requestData.getId());
        if (mockResource == null) {
            throw new MockerNotRegisteredException(requestData.getUrl());
        }
        return mockResource;
    }
}
