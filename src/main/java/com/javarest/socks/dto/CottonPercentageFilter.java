package com.javarest.socks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Model for cotton percentage filtering parameters, supporting range or comparison operators")
public class CottonPercentageFilter {

    @Schema(description = "Operator for filtering (> / < / =)", example = ">")
    private final String operator;

    @Schema(description = "Minimum value of the cotton percentage (used in range-based filter)", example = "30")
    private Integer minValue;

    @Schema(description = "Maximum value of the cotton percentage (used in range-based filter)", example = "90")
    private Integer maxValue;

    public CottonPercentageFilter(String filterExpression) {
        if(filterExpression.contains("-")) {
            String[] range = filterExpression.split("-");
            this.minValue = Integer.parseInt(range[0]);
            this.maxValue = Integer.parseInt(range[1]);
            this.operator = "range";
        } else if(filterExpression.contains(">")) {
            this.minValue = Integer.parseInt(filterExpression.split(">")[1]);
            this.operator = ">";
        } else if(filterExpression.contains("<")) {
            this.maxValue = Integer.parseInt(filterExpression.split("<")[1]);
            this.operator = "<";
        } else if(filterExpression.contains("=")) {
            this.minValue = Integer.parseInt(filterExpression.split("=")[1]);
            this.operator = "=";
        } else throw new IllegalArgumentException("Invalid operator in filter: " + filterExpression);
    }
    @Schema(description = "If true, uses range-based filtering (minValue to maxValue). False otherwise.", example = "true")
    public boolean isRange() {
        return "range".equals(operator);
    }
}
