package io.sevk.types;

import com.google.gson.annotations.SerializedName;

public enum SegmentRuleOperator {
    @SerializedName("equals")
    EQUALS,
    @SerializedName("notEquals")
    NOT_EQUALS,
    @SerializedName("contains")
    CONTAINS,
    @SerializedName("notContains")
    NOT_CONTAINS,
    @SerializedName("startsWith")
    STARTS_WITH,
    @SerializedName("endsWith")
    ENDS_WITH,
    @SerializedName("greaterThan")
    GREATER_THAN,
    @SerializedName("lessThan")
    LESS_THAN,
    @SerializedName("greaterOrEqual")
    GREATER_OR_EQUAL,
    @SerializedName("lessOrEqual")
    LESS_OR_EQUAL,
    @SerializedName("isEmpty")
    IS_EMPTY,
    @SerializedName("isNotEmpty")
    IS_NOT_EMPTY,
    @SerializedName("in")
    IN,
    @SerializedName("notIn")
    NOT_IN
}
