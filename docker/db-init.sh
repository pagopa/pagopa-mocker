#!/bin/bash

echo "########### Deleting previous tables ###########"
aws $AWS_ENDPOINT \
    dynamodb delete-table \
        --table-name pagopamockresource \
        --endpoint-url http://localhost:8000 \
        --region local

echo "########### Creating tables with global secondary index ###########"
aws   $AWS_ENDPOINT \
      dynamodb create-table \
         --table-name pagopamockresource \
         --endpoint-url http://localhost:8000 \
         --region local \
         --attribute-definitions \
           AttributeName=id,AttributeType=S \
           AttributeName=mockType,AttributeType=S \
           AttributeName=resourceUrl,AttributeType=S \
           AttributeName=httpMethod,AttributeType=S \
        --key-schema AttributeName=id,KeyType=HASH \
        --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
        --global-secondary-indexes \
                  "[
                      {
                          \"IndexName\": \"mockType_idx\",
                          \"KeySchema\": [{\"AttributeName\":\"mockType\",\"KeyType\":\"HASH\"}],
                          \"Projection\":{
                              \"ProjectionType\":\"ALL\"
                          },
                          \"ProvisionedThroughput\": {
                              \"ReadCapacityUnits\": 5,
                              \"WriteCapacityUnits\": 5
                          }
                      },
                      {
                          \"IndexName\": \"resourceUrl_idx\",
                          \"KeySchema\": [{\"AttributeName\":\"resourceUrl\",\"KeyType\":\"HASH\"}],
                          \"Projection\":{
                              \"ProjectionType\":\"ALL\"
                          },
                          \"ProvisionedThroughput\": {
                              \"ReadCapacityUnits\": 5,
                              \"WriteCapacityUnits\": 5
                          }
                      },
                      {
                          \"IndexName\": \"httpMethod_idx\",
                          \"KeySchema\": [{\"AttributeName\":\"httpMethod\",\"KeyType\":\"HASH\"}],
                          \"Projection\":{
                              \"ProjectionType\":\"ALL\"
                          },
                          \"ProvisionedThroughput\": {
                              \"ReadCapacityUnits\": 5,
                              \"WriteCapacityUnits\": 5
                          }
                      }
                  ]"




echo "########### Show the created tables ###########"
aws   $AWS_ENDPOINT \
      dynamodb describe-table --endpoint-url http://localhost:8000 --region local --table-name pagopamockresource --output table


echo "########### Inserting EC test data into a table ###########"
aws   $AWS_ENDPOINT \
      dynamodb put-item --endpoint-url http://localhost:8000 --region local --table-name pagopamockresource --item "{
  \"id\": {
    \"S\": \"mockecPOST\"
  },
  \"mockType\": {
    \"S\": \"mockec\"
  },
  \"resourceUrl\": {
    \"S\": \"/\"
  },
  \"httpMethod\": {
    \"S\": \"POST\"
  },
  \"name\": {
    \"S\": \"Mocked responses for communication with Nodo - EC flow\"
  },
  \"tag\": {
    \"L\": [
      {
        \"S\": \"SOAP\"
      },
      {
        \"S\": \"Nodo\"
      },
      {
        \"S\": \"EC\"
      }
    ]
  },
  \"rules\": {
    \"L\": [
      {
        \"M\": {
          \"name\": {
            \"S\": \"Verify Payment Notice - Notice number starts with '30215'\"
          },
          \"tag\": {
            \"L\": [
              {
                \"S\": \"Notice number\"
              }
            ]
          },
          \"isActive\": {
            \"BOOL\": true
          },
          \"conditions\": {
            \"L\": [
              {
                \"M\": {
                  \"fieldPosition\": {
                    \"S\": \"HEADER\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"STRING\"
                  },
                  \"fieldName\": {
                    \"S\": \"SOAPAction\"
                  },
                  \"conditionType\": {
                    \"S\": \"EQ\"
                  },
                  \"conditionValue\": {
                    \"S\": \"paVerifyPaymentNotice\"
                  }
                }
              },
              {
                \"M\": {
                  \"fieldPosition\": {
                    \"S\": \"BODY\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"XML\"
                  },
                  \"fieldName\": {
                    \"S\": \"paVerifyPaymentNoticeReq.qrCode.noticeNumber\"
                  },
                  \"conditionType\": {
                    \"S\": \"REGEX\"
                  },
                  \"conditionValue\": {
                    \"S\": \"^30215.*\"
                  }
                }
              }
            ]
          },
          \"response\": {
            \"M\": {
              \"body\": {
                \"S\": \"PHNvYXBlbnY6RW52ZWxvcGUgeG1sbnM6c29hcGVudj0iaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvc29hcC9lbnZlbG9wZS8iIHhtbG5zOnBhZj0iaHR0cDovL3BhZ29wYS1hcGkucGFnb3BhLmdvdi5pdC9wYS9wYUZvck5vZGUueHNkIj4KICA8c29hcGVudjpIZWFkZXIgLz4KICA8c29hcGVudjpCb2R5PgogICAgPHBhZjpwYVZlcmlmeVBheW1lbnROb3RpY2VSZXM+CiAgICAgIDxvdXRjb21lPk9LPC9vdXRjb21lPgogICAgICA8cGF5bWVudExpc3Q+CiAgICAgICAgPHBheW1lbnRPcHRpb25EZXNjcmlwdGlvbj4KICAgICAgICAgIDxhbW91bnQ+MTIwLjAwPC9hbW91bnQ+CiAgICAgICAgICA8b3B0aW9ucz5FUTwvb3B0aW9ucz4KICAgICAgICAgIDxkdWVEYXRlPjIwMjEtMDctMzE8L2R1ZURhdGU+CiAgICAgICAgICA8ZGV0YWlsRGVzY3JpcHRpb24+cGFnYW1lbnRvVGVzdDwvZGV0YWlsRGVzY3JpcHRpb24+CiAgICAgICAgICA8YWxsQ0NQPmZhbHNlPC9hbGxDQ1A+CiAgICAgICAgPC9wYXltZW50T3B0aW9uRGVzY3JpcHRpb24+CiAgICAgIDwvcGF5bWVudExpc3Q+CiAgICAgIDxwYXltZW50RGVzY3JpcHRpb24+UGFnYW1lbnRvIGRpIFRlc3Q8L3BheW1lbnREZXNjcmlwdGlvbj4KICAgICAgPGZpc2NhbENvZGVQQT4ke3BhVmVyaWZ5UGF5bWVudE5vdGljZVJlcS5pZFBBfTwvZmlzY2FsQ29kZVBBPgogICAgICA8Y29tcGFueU5hbWU+Y29tcGFueU5hbWU8L2NvbXBhbnlOYW1lPgogICAgICA8b2ZmaWNlTmFtZT5vZmZpY2VOYW1lPC9vZmZpY2VOYW1lPiAgICAgIAogICAgPC9wYWY6cGFWZXJpZnlQYXltZW50Tm90aWNlUmVzPgogIDwvc29hcGVudjpCb2R5Pgo8L3NvYXBlbnY6RW52ZWxvcGU+\"
              },
              \"parameters\": {
                \"L\": [
                  {
                    \"S\": \"paVerifyPaymentNoticeReq.idPA\"
                  }
                ]
              },
              \"status\": {
                \"N\": \"200\"
              },
              \"headers\": {
                \"M\": {
                  \"Content-Type\": {
                    \"S\": \"application/xml\"
                  },
                  \"x-powered-by\": {
                    \"S\": \"Mocker\"
                  }
                }
              }
            }
          }
        }
      },
      {
        \"M\": {
          \"name\": {
            \"S\": \"Verify Payment Notice - Notice number starts with '30299'\"
          },
          \"tag\": {
            \"L\": [
              {
                \"S\": \"Notice number\"
              }
            ]
          },
          \"isActive\": {
            \"BOOL\": true
          },
          \"conditions\": {
            \"L\": [
              {
                \"M\": {
                  \"fieldPosition\": {
                    \"S\": \"HEADER\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"STRING\"
                  },
                  \"fieldName\": {
                    \"S\": \"SOAPAction\"
                  },
                  \"conditionType\": {
                    \"S\": \"EQ\"
                  },
                  \"conditionValue\": {
                    \"S\": \"paVerifyPaymentNotice\"
                  }
                }
              },
              {
                \"M\": {
                  \"fieldPosition\": {
                    \"S\": \"BODY\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"XML\"
                  },
                  \"fieldName\": {
                    \"S\": \"paVerifyPaymentNoticeReq.qrCode.noticeNumber\"
                  },
                  \"conditionType\": {
                    \"S\": \"REGEX\"
                  },
                  \"conditionValue\": {
                    \"S\": \"^30299.*\"
                  }
                }
              }
            ]
          },
          \"response\": {
            \"M\": {
              \"body\": {
                \"S\": \"PHNvYXBlbnY6RW52ZWxvcGUgeG1sbnM6c29hcGVudj0iaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvc29hcC9lbnZlbG9wZS8iIHhtbG5zOnBhZj0iaHR0cDovL3BhZ29wYS1hcGkucGFnb3BhLmdvdi5pdC9wYS9wYUZvck5vZGUueHNkIj4KICA8c29hcGVudjpIZWFkZXIgLz4KICA8c29hcGVudjpCb2R5PgogICAgPHBhZjpwYVZlcmlmeVBheW1lbnROb3RpY2VSZXM+CiAgICAgIDxvdXRjb21lPktPPC9vdXRjb21lPgogICAgICA8ZmF1bHQ+CiAgICAgICAgPGZhdWx0Q29kZT5QQUFfUEFHQU1FTlRPX1NDQURVVE88L2ZhdWx0Q29kZT4KICAgICAgICA8ZmF1bHRTdHJpbmc+UGFnYW1lbnRvIGluIGF0dGVzYSByaXN1bHRhIHNjYWR1dG8gYWxs4oCZRW50ZSBDcmVkaXRvcmU8L2ZhdWx0U3RyaW5nPgogICAgICAgIDxpZD4ke3BhVmVyaWZ5UGF5bWVudE5vdGljZVJlcS5pZFBBfTwvaWQ+CiAgICAgICAgPGRlc2NyaXB0aW9uPmlsIG51bWVybyBhdnZpc28gJHtwYVZlcmlmeVBheW1lbnROb3RpY2VSZXEucXJDb2RlLm5vdGljZU51bWJlcn0gZScgc2NhZHV0bzwvZGVzY3JpcHRpb24+CiAgICAgIDwvZmF1bHQ+CiAgICA8L3BhZjpwYVZlcmlmeVBheW1lbnROb3RpY2VSZXM+CiAgPC9zb2FwZW52OkJvZHk+Cjwvc29hcGVudjpFbnZlbG9wZT4=\"
              },
              \"parameters\": {
                \"L\": [
                  {
                    \"S\": \"paVerifyPaymentNoticeReq.idPA\"
                  },
                  {
                    \"S\": \"paVerifyPaymentNoticeReq.qrCode.noticeNumber\"
                  }
                ]
              },
              \"status\": {
                \"N\": \"200\"
              },
              \"headers\": {
                \"M\": {
                  \"Content-Type\": {
                    \"S\": \"application/xml\"
                  },
                  \"x-powered-by\": {
                    \"S\": \"Mocker\"
                  }
                }
              }
            }
          }
        }
      }
    ]
  }
}"


echo "########### Inserting PSP test data into a table ###########"
aws   $AWS_ENDPOINT \
      dynamodb put-item --endpoint-url http://localhost:8000 --region local --table-name pagopamockresource --item "{
  \"id\": {
    \"S\": \"mockpspPOST\"
  },
  \"mockType\": {
    \"S\": \"mockpsp\"
  },
  \"resourceUrl\": {
    \"S\": \"/\"
  },
  \"httpMethod\": {
    \"S\": \"POST\"
  },
  \"name\": {
    \"S\": \"Mocked responses for communication with Nodo - PSP flow\"
  },
  \"tag\": {
    \"L\": [
      {
        \"S\": \"SOAP\"
      },
      {
        \"S\": \"Nodo\"
      },
      {
        \"S\": \"PSP\"
      }
    ]
  },
  \"rules\": {
    \"L\": [
      {
        \"M\": {
          \"id\": {
            \"S\": \"829a348c-00a0-44b4-4430-ababababa\"
          },
          \"name\": {
            \"S\": \"Send RPT to PSP - OK response\"
          },
          \"tag\": {
            \"L\": [
              {
                \"S\": \"Send RPT\"
              }
            ]
          },
          \"isActive\": {
            \"BOOL\": true
          },
          \"conditions\": {
            \"L\": [
              {
                \"M\": {
                  \"id\": {
                    \"S\": \"829a348c-00a0-44b4-4430-bbbbaaaa\"
                  },
                  \"fieldPosition\": {
                    \"S\": \"HEADER\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"STRING\"
                  },
                  \"fieldName\": {
                    \"S\": \"SOAPAction\"
                  },
                  \"conditionType\": {
                    \"S\": \"EQ\"
                  },
                  \"conditionValue\": {
                    \"S\": \"pspInviaRPT\"
                  }
                }
              }
            ]
          },
          \"response\": {
            \"M\": {
              \"id\": {
                \"S\": \"829a348c-00a0-44b4-4430-babababa\"
              },
              \"body\": {
                \"S\": \"PHNvYXBlbnY6RW52ZWxvcGUKICAgIHhtbG5zOnNvYXBlbnY9Imh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3NvYXAvZW52ZWxvcGUvIgogICAgeG1sbnM6d3M9Imh0dHA6Ly93cy5wYWdhbWVudGkudGVsZW1hdGljaS5nb3YvIj4KICAgIDxzb2FwZW52OkhlYWRlci8+CiAgICA8c29hcGVudjpCb2R5PgogICAgICAgIDx3czpwc3BJbnZpYVJQVFJlc3BvbnNlPgogICAgICAgICAgICA8cHNwSW52aWFSUFRSZXNwb25zZT4KICAgICAgICAgICAgICAgIDxlc2l0b0NvbXBsZXNzaXZvT3BlcmF6aW9uZT5PSzwvZXNpdG9Db21wbGVzc2l2b09wZXJhemlvbmU+CiAgICAgICAgICAgICAgICA8aWRlbnRpZmljYXRpdm9DYXJyZWxsbz5jYXJ0MTYzNjYzODM5Nzc4NTIwODc8L2lkZW50aWZpY2F0aXZvQ2FycmVsbG8+CiAgICAgICAgICAgICAgICA8cGFyYW1ldHJpUGFnYW1lbnRvSW1tZWRpYXRvPmlkQnJ1Y2lhdHVyYT0xNjM2NjM4Mzk3Nzg1MjA4NzwvcGFyYW1ldHJpUGFnYW1lbnRvSW1tZWRpYXRvPgogICAgICAgICAgICA8L3BzcEludmlhUlBUUmVzcG9uc2U+CiAgICAgICAgPC93czpwc3BJbnZpYVJQVFJlc3BvbnNlPgogICAgPC9zb2FwZW52OkJvZHk+CiAgPC9zb2FwZW52OkVudmVsb3BlPg==\"
              },
              \"status\": {
                \"N\": \"200\"
              },
              \"headers\": {
                \"M\": {
                  \"Content-Type\": {
                    \"S\": \"application/xml\"
                  },
                  \"x-powered-by\": {
                    \"S\": \"Mocker\"
                  }
                }
              }
            }
          }
        }
      },
      {
        \"M\": {
          \"name\": {
            \"S\": \"Send RPT to PSP - KO response\"
          },
          \"tag\": {
            \"L\": [
              {
                \"S\": \"Send RPT\"
              }
            ]
          },
          \"isActive\": {
            \"BOOL\": false
          },
          \"conditions\": {
            \"L\": [
              {
                \"M\": {
                  \"fieldPosition\": {
                    \"S\": \"HEADER\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"STRING\"
                  },
                  \"fieldName\": {
                    \"S\": \"SOAPAction\"
                  },
                  \"conditionType\": {
                    \"S\": \"EQ\"
                  },
                  \"conditionValue\": {
                    \"S\": \"pspInviaRPT\"
                  }
                }
              }
            ]
          },
          \"response\": {
            \"M\": {
              \"body\": {
                \"S\": \"PHNvYXBlbnY6RW52ZWxvcGUKICAgIHhtbG5zOnNvYXBlbnY9Imh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3NvYXAvZW52ZWxvcGUvIgogICAgeG1sbnM6d3M9Imh0dHA6Ly93cy5wYWdhbWVudGkudGVsZW1hdGljaS5nb3YvIj4KICAgIDxzb2FwZW52OkhlYWRlci8+CiAgICA8c29hcGVudjpCb2R5PgogICAgICAgIDx3czpwc3BJbnZpYVJQVFJlc3BvbnNlPgogICAgICAgICAgICA8cHNwSW52aWFSUFRSZXNwb25zZT4KICAgICAgICAgICAgICAgIDxlc2l0b0NvbXBsZXNzaXZvT3BlcmF6aW9uZT5LTzwvZXNpdG9Db21wbGVzc2l2b09wZXJhemlvbmU+CiAgICAgICAgICAgIDwvcHNwSW52aWFSUFRSZXNwb25zZT4KICAgICAgICA8L3dzOnBzcEludmlhUlBUUmVzcG9uc2U+CiAgICA8L3NvYXBlbnY6Qm9keT4KICA8L3NvYXBlbnY6RW52ZWxvcGU+\"
              },
              \"status\": {
                \"N\": \"500\"
              },
              \"headers\": {
                \"M\": {
                  \"Content-Type\": {
                    \"S\": \"application/xml\"
                  },
                  \"x-powered-by\": {
                    \"S\": \"Mocker\"
                  }
                }
              }
            }
          }
        }
      },
      {
        \"M\": {
          \"name\": {
            \"S\": \"Ask RT list from PSP - OK response\"
          },
          \"tag\": {
            \"L\": [
              {
                \"S\": \"Ask RT list\"
              }
            ]
          },
          \"isActive\": {
            \"BOOL\": true
          },
          \"conditions\": {
            \"L\": [
              {
                \"M\": {
                  \"fieldPosition\": {
                    \"S\": \"HEADER\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"STRING\"
                  },
                  \"fieldName\": {
                    \"S\": \"SOAPAction\"
                  },
                  \"conditionType\": {
                    \"S\": \"EQ\"
                  },
                  \"conditionValue\": {
                    \"S\": \"pspChiediListaRT\"
                  }
                }
              },
              {
                \"M\": {
                  \"fieldPosition\": {
                    \"S\": \"BODY\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"XML\"
                  },
                  \"fieldName\": {
                    \"S\": \"pspChiediListaRT.identificativoRichiedente\"
                  },
                  \"conditionType\": {
                    \"S\": \"EQ\"
                  },
                  \"conditionValue\": {
                    \"S\": \"77777777777\"
                  }
                }
              }
            ]
          },
          \"response\": {
            \"M\": {
              \"body\": {
                \"S\": \"PHNvYXBlbnY6RW52ZWxvcGUgeG1sbnM6c29hcGVudj0iaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvc29hcC9lbnZlbG9wZS8iIHhtbG5zOndzPSJodHRwOi8vd3MucGFnYW1lbnRpLnRlbGVtYXRpY2kuZ292LyI+CiAgICA8c29hcGVudjpIZWFkZXIvPgogICAgPHNvYXBlbnY6Qm9keT4KICAgICAgPHdzOnBzcENoaWVkaUxpc3RhUlQ+CiAgICAgICAgICA8cHNwQ2hpZWRpTGlzdGFSVFJlc3BvbnNlPgogICAgICAgICAgICA8ZWxlbWVudG9MaXN0YVJUUmVzcG9uc2U+CiAgICAgICAgICAgICAgICA8aWRlbnRpZmljYXRpdm9Eb21pbmlvPiR7cHNwQ2hpZWRpTGlzdGFSVC5pZGVudGlmaWNhdGl2b1JpY2hpZWRlbnRlfTwvaWRlbnRpZmljYXRpdm9Eb21pbmlvPgogICAgICAgICAgICAgICAgPGlkZW50aWZpY2F0aXZvVW5pdm9jb1ZlcnNhbWVudG8+cmFuZG9tSXV2PC9pZGVudGlmaWNhdGl2b1VuaXZvY29WZXJzYW1lbnRvPgogICAgICAgICAgICAgICAgPGNvZGljZUNvbnRlc3RvUGFnYW1lbnRvPjE1ODMxNDg3NTU2MDM8L2NvZGljZUNvbnRlc3RvUGFnYW1lbnRvPgogICAgICAgICAgICA8L2VsZW1lbnRvTGlzdGFSVFJlc3BvbnNlPgogICAgICAgICAgPC9wc3BDaGllZGlMaXN0YVJUUmVzcG9uc2U+CiAgICAgIDwvd3M6cHNwQ2hpZWRpTGlzdGFSVFJlc3BvbnNlPgogICAgPC9zb2FwZW52OkJvZHk+CiAgPC9zb2FwZW52OkVudmVsb3BlPg==\"
              },
              \"status\": {
                \"N\": \"200\"
              },
              \"parameters\": {
                \"L\": [
                  {
                    \"S\": \"pspChiediListaRT.identificativoRichiedente\"
                  }
                ]
              },
              \"headers\": {
                \"M\": {
                  \"Content-Type\": {
                    \"S\": \"application/xml\"
                  },
                  \"x-powered-by\": {
                    \"S\": \"Mocker\"
                  }
                }
              }
            }
          }
        }
      },
      {
        \"M\": {
          \"name\": {
            \"S\": \"Ask RT list from PSP - KO response\"
          },
          \"tag\": {
            \"L\": [
              {
                \"S\": \"Ask RT list\"
              }
            ]
          },
          \"isActive\": {
            \"BOOL\": true
          },
          \"conditions\": {
            \"L\": [
              {
                \"M\": {
                  \"fieldPosition\": {
                    \"S\": \"HEADER\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"STRING\"
                  },
                  \"fieldName\": {
                    \"S\": \"SOAPAction\"
                  },
                  \"conditionType\": {
                    \"S\": \"EQ\"
                  },
                  \"conditionValue\": {
                    \"S\": \"pspChiediListaRT\"
                  }
                }
              },
              {
                \"M\": {
                  \"fieldPosition\": {
                    \"S\": \"BODY\"
                  },
                  \"analyzedContentType\": {
                    \"S\": \"XML\"
                  },
                  \"fieldName\": {
                    \"S\": \"pspChiediListaRT.identificativoRichiedente\"
                  },
                  \"conditionType\": {
                    \"S\": \"NEQ\"
                  },
                  \"conditionValue\": {
                    \"S\": \"77777777777\"
                  }
                }
              }
            ]
          },
          \"response\": {
            \"M\": {
              \"body\": {
                \"S\": \"PHNvYXBlbnY6RW52ZWxvcGUgeG1sbnM6c29hcGVudj0iaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvc29hcC9lbnZlbG9wZS8iIHhtbG5zOndzPSJodHRwOi8vd3MucGFnYW1lbnRpLnRlbGVtYXRpY2kuZ292LyI+CiAgICA8c29hcGVudjpIZWFkZXIvPgogICAgPHNvYXBlbnY6Qm9keT4KICAgICAgPHdzOnBzcENoaWVkaUxpc3RhUlQ+CiAgICAgICAgICA8cHNwQ2hpZWRpTGlzdGFSVFJlc3BvbnNlPgogICAgICAgICAgICA8b3V0Y29tZT5LTzwvb3V0Y29tZT4KICAgICAgICAgIDwvcHNwQ2hpZWRpTGlzdGFSVFJlc3BvbnNlPgogICAgICA8L3dzOnBzcENoaWVkaUxpc3RhUlRSZXNwb25zZT4KICAgIDwvc29hcGVudjpCb2R5PgogIDwvc29hcGVudjpFbnZlbG9wZT4=\"
              },
              \"status\": {
                \"N\": \"500\"
              },
              \"headers\": {
                \"M\": {
                  \"Content-Type\": {
                    \"S\": \"application/xml\"
                  },
                  \"x-powered-by\": {
                    \"S\": \"Mocker\"
                  }
                }
              }
            }
          }
        }
      }
    ]
  }
}"


echo "########### Inserting ApiConfig test data into a table ###########"
aws   $AWS_ENDPOINT \
      dynamodb put-item --endpoint-url http://localhost:8000 --region local --table-name pagopamockresource --item "{
  \"id\": {
    \"S\": \"apicfgbrokers11111GET\"
  },
  \"mockType\": {
    \"S\": \"api-cfg\"
  },
  \"resourceUrl\": {
    \"S\": \"/brokers/11111\"
  },
  \"httpMethod\": {
    \"S\": \"GET\"
  },
  \"name\": {
    \"S\": \"Mocked responses for communication with ApiConfig - Broker 11111\"
  },
  \"tag\": {
    \"L\": [
      {
        \"S\": \"REST\"
      },
      {
        \"S\": \"ApiConfig\"
      },
      {
        \"S\": \"Broker\"
      }
    ]
  },
  \"rules\": {
    \"L\": [
      {
        \"M\": {
          \"id\": {
            \"S\": \"19caf3bc-7aa9-4f47-a99c-38ce59d72024\"
          },
          \"name\": {
            \"S\": \"Main rule for retrieving broker with id=11111\"
          },
          \"tag\": {
            \"L\": [
              {
                \"S\": \"Broker\"
              }
            ]
          },
          \"isActive\": {
            \"BOOL\": true
          },
          \"conditions\": {
            \"L\": []
          },
          \"response\": {
            \"M\": {
              \"id\": {
                \"S\": \"1c574899-f5f7-4ccf-b355-30a6d2454f8b\"
              },
              \"body\": {
                \"S\": \"ewogICAgImJyb2tlcl9jb2RlIjogIjExMTExIiwKICAgICJlbmFibGVkIjogdHJ1ZSwKICAgICJkZXNjcmlwdGlvbiI6ICJSZWdpb25lIExhemlvIiwKICAgICJleHRlbmRlZF9mYXVsdF9iZWFuIjogZmFsc2UKfQ==\"
              },
              \"status\": {
                \"N\": \"200\"
              },
              \"headers\": {
                \"M\": {
                  \"Content-Type\": {
                    \"S\": \"application/json\"
                  },
                  \"x-powered-by\": {
                    \"S\": \"Mocker\"
                  }
                }
              }
            }
          }
        }
      }
    ]
  }
}"