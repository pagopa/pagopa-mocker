package it.gov.pagopa.mocker.service.validator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import it.gov.pagopa.mocker.entity.*;
import it.gov.pagopa.mocker.exception.MockerNotCompliantRequestException;
import it.gov.pagopa.mocker.exception.MockerParseRequestException;
import it.gov.pagopa.mocker.exception.MockerScriptExecutionException;
import it.gov.pagopa.mocker.model.JSONUnmarshalledBody;
import it.gov.pagopa.mocker.service.scripting.ScriptExecutor;
import it.gov.pagopa.mocker.util.Constants;
import it.gov.pagopa.mocker.util.Utility;
import it.gov.pagopa.mocker.util.ConditionValidator;
import it.gov.pagopa.mocker.util.XMLParser;
import it.gov.pagopa.mocker.model.ExtractedRequest;
import it.gov.pagopa.mocker.model.ExtractedResponse;
import it.gov.pagopa.mocker.model.UnmarshalledBody;
import it.gov.pagopa.mocker.model.enumeration.ConditionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional
@Slf4j
public class ResourceExtractor {

    @Autowired
    private ScriptExecutor scriptExecutor;

    private final XMLParser xmlParser;

    private final Gson jsonParser;

    public ResourceExtractor() throws ParserConfigurationException {
        this.xmlParser = new XMLParser();
        this.jsonParser = new Gson();
    }

    public ExtractedResponse extract(ExtractedRequest requestData, MockResourceEntity mockResource) throws MockerParseRequestException, MockerNotCompliantRequestException {
        ExtractedResponse extractedResponse;
        try {
            UnmarshalledBody unmarshalledBody = extractBodyFields(requestData.getBody(), requestData.getContentType());
            MockRuleEntity mockRule = getValidMockRule(mockResource, requestData, unmarshalledBody);
            extractedResponse = getMockResponse(mockRule, unmarshalledBody, requestData);
        } catch (IOException | SAXException | JsonSyntaxException e) {
            throw new MockerParseRequestException(e);
        }
        return extractedResponse;
    }

    public UnmarshalledBody extractBodyFields(String body, String contentType) throws IOException, SAXException, JsonSyntaxException {
        UnmarshalledBody unmarshalledBody = null;
        String[] normalizedContentType = contentType.split(";");
        if (!Utility.isNullOrEmpty(body) && normalizedContentType.length > 0) {
            switch (normalizedContentType[0]) {
                case Constants.APPLICATION_JSON:
                    unmarshalledBody = new JSONUnmarshalledBody(jsonParser.fromJson(body, Map.class));
                    break;
                case Constants.APPLICATION_XML:
                case Constants.TEXT_XML:
                    unmarshalledBody = xmlParser.parse(body);
                    break;
                default:
                    unmarshalledBody = new JSONUnmarshalledBody(Map.of(Constants.STRING_CONTENT_KEY, body));
                    break;
            }
        }
        log.debug(String.format("The request body extracted and unmarshalled as [%s] content type is the following: [%s].", contentType, unmarshalledBody));
        return unmarshalledBody;
    }

    private MockRuleEntity getValidMockRule(MockResourceEntity mockResource, ExtractedRequest request, UnmarshalledBody unmarshalledBody) throws MockerNotCompliantRequestException {
        List<MockRuleEntity> mockRules = mockResource.getRules().stream().sorted(Comparator.comparingInt(MockRuleEntity::getOrder)).collect(Collectors.toList());
        int numberOfMockRules = mockRules.size();
        MockRuleEntity result = null;
        int ruleIndex = 0;
        boolean isRuleFound = false;
        while (canContinueRuleValidation(isRuleFound, ruleIndex, numberOfMockRules)) {
            MockRuleEntity mockRule = mockRules.get(ruleIndex);
            if (mockRule.isActive()) {
                log.debug(String.format("Analyzing the mock rule [%s]: [%s].", mockRule.getId(), mockRule.getName()));
                List<MockConditionEntity> mockConditions = mockRule.getConditions();
                int numberOfMockConditions = mockConditions.size();
                boolean isRuleStillValid = true;
                int conditionIndex = 0;
                while (canContinueValidationForRemainingConditions(isRuleStillValid, conditionIndex, numberOfMockConditions)) {
                    MockConditionEntity mockCondition = mockConditions.get(conditionIndex);
                    isRuleStillValid = evaluateMockCondition(mockCondition, request, unmarshalledBody);
                    conditionIndex++;
                }
                if (isRuleStillValid) {
                    result = mockRule;
                    isRuleFound = true;
                }
            }
            ruleIndex++;
        }
        if (result == null) {
            throw new MockerNotCompliantRequestException(request.getUrl());
        }
        return result;
    }

    private boolean canContinueRuleValidation(boolean isRuleFound, int ruleIndex, int numberOfMockRules) {
        return !isRuleFound && ruleIndex < numberOfMockRules;
    }

    private boolean canContinueValidationForRemainingConditions(boolean isRuleStillValid, int conditionIndex, int numberOfMockConditions) {
        return isRuleStillValid && conditionIndex < numberOfMockConditions;
    }

    private boolean evaluateMockCondition(MockConditionEntity mockCondition, ExtractedRequest request, UnmarshalledBody unmarshalledBody) throws MockerNotCompliantRequestException {
        boolean isValid = false;
        switch (mockCondition.getFieldPosition()) {
            case BODY:
                isValid = isBodyRequestSatisfyingRequirements(mockCondition, unmarshalledBody, request);
                break;
            case URL:
                isValid = isURLSatisfyingRequirements(mockCondition, request);
                break;
            case HEADER:
                isValid = isHeaderSatisfyingRequirements(mockCondition, request);
                break;
        }
        return isValid;
    }

    private boolean isBodyRequestSatisfyingRequirements(MockConditionEntity mockCondition, UnmarshalledBody unmarshalledBody, ExtractedRequest request) throws MockerNotCompliantRequestException {
        log.debug(String.format("Evaluating the mock condition: [%s]: [%s %s %s].", mockCondition.getId(), mockCondition.getFieldName(), mockCondition.getConditionType(), mockCondition.getConditionValue()));
        boolean isValid = false;
        if (unmarshalledBody != null) {
            switch (mockCondition.getAnalyzedContentType()) {
                case JSON, XML:
                    isValid = isFieldCompliantToCondition(mockCondition, unmarshalledBody);
                    break;
                case STRING:
                    isValid = isContentCompliantToCondition(unmarshalledBody.getFieldValue(Constants.STRING_CONTENT_KEY), mockCondition.getConditionValue(), mockCondition.getConditionType());
                    break;
            }
        }
        return isValid;
    }

    private boolean isURLSatisfyingRequirements(MockConditionEntity mockCondition, ExtractedRequest request) {
        boolean isValid = false;
        Map<String, String> queryStringParameters = request.getQueryParameters();
        if (queryStringParameters != null) {
            ConditionType conditionType = mockCondition.getConditionType();
            String conditionValue = mockCondition.getConditionValue();
            String urlValue = queryStringParameters.get(mockCondition.getFieldName());
            log.debug(String.format("Evaluating the mock condition for query parameter: [%s: %s %s %s].", mockCondition.getId(), urlValue, conditionType, conditionValue));
            isValid = isContentCompliantToCondition(urlValue, conditionValue, conditionType);
        }
        return isValid;
    }

    private boolean isHeaderSatisfyingRequirements(MockConditionEntity mockCondition, ExtractedRequest request) {
        boolean isValid = false;
        if (request.getHeaders() != null) {
            ConditionType conditionType = mockCondition.getConditionType();
            String conditionValue = mockCondition.getConditionValue();
            String headerValue = request.getHeaders().get(mockCondition.getFieldName());
            log.debug(String.format("Evaluating the mock condition for header: [%s: %s %s %s].", mockCondition.getId(), headerValue, conditionType, conditionValue));
            isValid = isContentCompliantToCondition(headerValue, conditionValue, conditionType);
        }
        return isValid;
    }

    private boolean isFieldCompliantToCondition(MockConditionEntity mockCondition, UnmarshalledBody unmarshalledBody) {
        Object bodyFieldValue = unmarshalledBody.getFieldValue(mockCondition.getFieldName());
        ConditionType conditionType = mockCondition.getConditionType();
        String conditionValue = mockCondition.getConditionValue();
        return isContentCompliantToCondition(bodyFieldValue, conditionValue, conditionType);
    }

    private boolean isContentCompliantToCondition(Object fieldValue, String conditionValue, ConditionType conditionType) {
        boolean isValid = ConditionValidator.validate(fieldValue, conditionValue, conditionType);
        log.debug(String.format("Evaluated the mock condition for value: [%s]. Is compliant to this condition? [%s].", fieldValue, isValid));
        return isValid;
    }


    private ExtractedResponse getMockResponse(MockRuleEntity mockRule, UnmarshalledBody unmarshalledBody, ExtractedRequest requestData) {
        MockResponseEntity mockResponse = mockRule.getResponse();
        Map<String, String> dynamicParameters = executeScript(mockRule, unmarshalledBody);
        List<String> parameters = mockResponse.getParameters();
        String decodedBody = Utility.decodeBase64(mockResponse.getBody());
        // injecting request body's fields in response
        if (parameters != null && unmarshalledBody != null) {
            for (String parameterName : parameters) {
                String parameterValue = (String) unmarshalledBody.getFieldValue(parameterName);
                decodedBody = injectParameterValueInDecodedBody(decodedBody, parameterName, parameterValue);
            }
        }
        // injecting request query parameters in response
        if (parameters != null && requestData.getQueryParameters() != null) {
            for (String parameterName : parameters) {
                String parameterValue = requestData.getQueryParameters().get(parameterName);
                decodedBody = injectParameterValueInDecodedBody(decodedBody, parameterName, parameterValue);
            }
        }
        //
        for (Map.Entry<String, String> parameter : dynamicParameters.entrySet()) {
            decodedBody = injectParameterValueInDecodedBody(decodedBody, "dynamic." + parameter.getKey(), parameter.getValue());
        }
        //
        Map<String, String> headers = new HashMap<>();
        for (ResponseHeaderEntity headerPair : mockResponse.getHeaders()) {
            headers.put(headerPair.getHeader(), headerPair.getValue());
        }
        return ExtractedResponse.builder()
                .body(decodedBody)
                .isCacheable(mockResponse.getIsCacheable() == null || mockResponse.getIsCacheable())
                .status(mockResponse.getStatus())
                .headers(headers)
                .build();
    }

    private Map<String, String> executeScript(MockRuleEntity mockRule, UnmarshalledBody unmarshalledBody) {
        Map<String, String> dynamicResultParameters = new HashMap<>();
        ScriptingEntity scriptingEntity = mockRule.getScripting();
        if (scriptingEntity != null && Boolean.TRUE.equals(scriptingEntity.getIsActive())) {
            try {
                Map<String, Object> parameters = new HashMap<>();
                for (NameValueEntity parameter : scriptingEntity.getParameters()) {
                    String parameterValue = parameter.getValue();
                    if (parameter.getValue().startsWith("${")) {
                        String injectedField = parameter.getValue().replace("${", "").replace("}", "");
                        if (unmarshalledBody != null) {
                            Object fieldValue = unmarshalledBody.getFieldValue(injectedField);
                            parameterValue = fieldValue != null ? fieldValue.toString() : "undefined";
                        } else {
                            parameterValue = "undefined";
                        }
                    }
                    parameters.put(parameter.getName(), parameterValue);
                }
                dynamicResultParameters = scriptExecutor.execute(scriptingEntity.getScriptName(), parameters);
            } catch (MockerScriptExecutionException e) {
                log.warn("The execution of the scripts ended unsuccessfully. Returning an empty map of result fields.");
            }
        }
        return dynamicResultParameters;
    }

    private String injectParameterValueInDecodedBody(String decodedBody, String name, String value) {
        try {
            if (value != null) {
                String regex = "\\$\\{" + name.replace(".", "\\.") + "\\}";
                decodedBody = decodedBody.replaceAll(regex, value);
            }
        } catch (IllegalArgumentException e) {
            log.warn(String.format("The injection of the value of [%s] parameter in decoded body was not executed correctly.", name));
        }
        return decodedBody;
    }
}
