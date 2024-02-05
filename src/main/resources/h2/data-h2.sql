INSERT INTO mocker.mock_resource(id, subsystem_url, resource_url, action, http_method, "name", is_active) VALUES
('ee743fac8ef18b7915c4213d1de18923', 'gpd-reporting-orgs-enrollment/api/v1', 'organizations/77777777777', null, 'GET', 'Get enrolled organization with ID 77777777777', true),
('495983b742d7e4bfc9afcbe002e2f07d', 'gpd-reporting-orgs-enrollment/api/v1', 'organizations/88888888888', null, 'GET', 'Get enrolled organization with ID 88888888888', true),
('fb7adf5bce2afe832d407a05054607a7', 'gpd-reporting-orgs-enrollment/api/v1', 'organizations/99999999999', null, 'GET', 'Get enrolled organization with ID 99999999999', true);



INSERT INTO mocker.mock_response(id, body, status) VALUES
('26e05a1f-9621-4e24-a57d-28694ff30306', 'ewogICAgIm9yZ2FuaXphdGlvbkZpc2NhbENvZGUiOiAiNzc3Nzc3Nzc3NzciLAogICAgIm9yZ2FuaXphdGlvbk9uYm9hcmRpbmdEYXRlIjogIjIwMjMtMDYtMjBUMTU6MDM6NTYuODYyNjQxIgp9', 200),
('26e05a1f-9621-4e24-a57d-28694ff30307', 'ewogICAgInN0YXR1cyI6IDQwNCwKICAgICJ0aXRsZSI6ICJOb3QgZm91bmQiLAogICAgIm1lc3NhZ2UiOiAiTm8gdmFsaWQgb3JnYW5pemF0aW9uIGZvdW5kIHdpdGggaWQgWzg4ODg4ODg4ODg4XSIsCn0=', 404),
('26e05a1f-9621-4e24-a57d-28694ff30308', 'ewogICAgIm9yZ2FuaXphdGlvbkZpc2NhbENvZGUiOiAiOTk5OTk5OTk5OTkiLAogICAgIm9yZ2FuaXphdGlvbk9uYm9hcmRpbmdEYXRlIjogIjIwMjMtMTItMDFUMDg6MDA6MDAuMDAwIgp9', 200);



INSERT INTO mocker.response_header(response_id, "header", "value") VALUES
('26e05a1f-9621-4e24-a57d-28694ff30306', 'Content-Type', 'application/json'),
('26e05a1f-9621-4e24-a57d-28694ff30306', 'X-Some-Header', 'some-strange-header-abcd'),
('26e05a1f-9621-4e24-a57d-28694ff30307', 'Content-Type', 'application/json'),
('26e05a1f-9621-4e24-a57d-28694ff30308', 'Content-Type', 'application/json'),
('26e05a1f-9621-4e24-a57d-28694ff30308', 'X-Some-Header', 'some-strange-header-wxyz');



INSERT INTO mocker.mock_rule(id, "name", "order", is_active, resource_id, response_id) VALUES
('6c08a21c-6a92-4f6b-a1e1-bf68c4e099c9', 'Main rule', 1, true, 'ee743fac8ef18b7915c4213d1de18923', '26e05a1f-9621-4e24-a57d-28694ff30306'),
('6c08a21c-6a92-4f6b-a1e1-bf68c4e099d0', 'Main rule', 1, true, '495983b742d7e4bfc9afcbe002e2f07d', '26e05a1f-9621-4e24-a57d-28694ff30307'),
('6c08a21c-6a92-4f6b-a1e1-bf68c4e099d9', 'Main rule', 1, true, 'fb7adf5bce2afe832d407a05054607a7', '26e05a1f-9621-4e24-a57d-28694ff30308');



INSERT INTO mocker.mock_condition(id, "order", field_position, content_type, field_name, condition_type, condition_value, rule_id) VALUES
('6b0b003d-74f4-428e-b950-61f42e02bf07', 1, 'HEADER', 'STRING', 'ClientId', 'NULL', '', '6c08a21c-6a92-4f6b-a1e1-bf68c4e099c9'),
('6b0b003d-74f4-428e-b950-61f42e02bf08', 1, 'HEADER', 'STRING', 'ClientId', 'NULL', '', '6c08a21c-6a92-4f6b-a1e1-bf68c4e099d0'),
('6b0b003d-74f4-428e-b950-61f42e02bf09', 1, 'HEADER', 'STRING', 'ClientId', 'NULL', '', '6c08a21c-6a92-4f6b-a1e1-bf68c4e099d9');
