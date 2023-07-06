INSERT INTO mocker.tag(id, "value") VALUES('398ddf77-8012-4767-84aa-87ad194d3ac9', 'EC');
INSERT INTO mocker.tag(id, "value") VALUES('261af795-2652-401e-a9c2-dcc160041bf6', 'GPD');
INSERT INTO mocker.tag(id, "value") VALUES('eaa80a30-2992-47fc-b607-4a248d03066b', 'ORGS ENROLLMENT');
INSERT INTO mocker.tag(id, "value") VALUES('88513e6d-7229-43ea-96b5-f83ffd96d5f5', 'MAIN RULE');



INSERT INTO mocker.mock_resource(id, resource_id, subsystem_url, resource_url, http_method, "name", is_active)
VALUES('70aa0abb-0001-0212-aa01-2cb1a0f00301', 'gpdreportingorgsenrollmentapiv1organizations77777777777get', 'gpd-reporting-orgs-enrollment/api/v1', 'organizations/77777777777', 'GET', 'Get enrolled organization with ID 77777777777', true);



INSERT INTO mocker.mock_response(id, body, status)
VALUES('26e05a1f-9621-4e24-a57d-28694ff30306', 'ewogICAgIm9yZ2FuaXphdGlvbkZpc2NhbENvZGUiOiAiNzc3Nzc3Nzc3NzciLAogICAgIm9yZ2FuaXphdGlvbk9uYm9hcmRpbmdEYXRlIjogIjIwMjMtMDYtMjBUMTU6MDM6NTYuODYyNjQxIgp9', 200);



INSERT INTO mocker.response_header(response_id, "header", "value")
VALUES('26e05a1f-9621-4e24-a57d-28694ff30306', 'Content-Type', 'application/json');



INSERT INTO mocker.mock_rule(id, "name", "order", is_active, resource_id, response_id)
VALUES('6c08a21c-6a92-4f6b-a1e1-bf68c4e099c9', 'Main rule', 1, true, '70aa0abb-0001-0212-aa01-2cb1a0f00301', '26e05a1f-9621-4e24-a57d-28694ff30306');



INSERT INTO mocker.mock_condition(id, "order", field_position, content_type, field_name, condition_type, condition_value, rule_id)
VALUES('6b0b003d-74f4-428e-b950-61f42e02bf07', 1, 'HEADER', 'STRING', 'ClientId', 'NULL', '', '6c08a21c-6a92-4f6b-a1e1-bf68c4e099c9');



INSERT INTO mocker.mock_resource_tag(mock_resource_id, tag_id)
VALUES('70aa0abb-0001-0212-aa01-2cb1a0f00301', '398ddf77-8012-4767-84aa-87ad194d3ac9');
INSERT INTO mocker.mock_resource_tag(mock_resource_id, tag_id)
VALUES('70aa0abb-0001-0212-aa01-2cb1a0f00301', '261af795-2652-401e-a9c2-dcc160041bf6');
INSERT INTO mocker.mock_resource_tag(mock_resource_id, tag_id)
VALUES('70aa0abb-0001-0212-aa01-2cb1a0f00301', 'eaa80a30-2992-47fc-b607-4a248d03066b');



INSERT INTO mocker.mock_rule_tag(mock_rule_id, tag_id)
VALUES('6c08a21c-6a92-4f6b-a1e1-bf68c4e099c9', '88513e6d-7229-43ea-96b5-f83ffd96d5f5');
