/* eslint-disable functional/prefer-readonly-type */
import {
  ConditionType,
  ContentType,
  RuleFieldPosition,
} from "../types/enumerations";

export class MockCondition {
  public id: string;
  public fieldPosition: RuleFieldPosition;
  public analyzedContentType: ContentType;
  public fieldName: string;
  public conditionType: ConditionType;
  public conditionValue: string;
}
