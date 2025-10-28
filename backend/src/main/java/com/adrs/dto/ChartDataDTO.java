package com.adrs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for chart data.
 * Contains data formatted for Chart.js pie charts and line graphs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDTO {

    private List<String> labels;
    private List<Long> data;
    private String chartType; // "pie", "line", "bar"

    /**
     * Constructor for simple chart data.
     *
     * @param labels the labels for chart
     * @param data the data values
     */
    public ChartDataDTO(List<String> labels, List<Long> data) {
        this.labels = labels;
        this.data = data;
        this.chartType = "pie";
    }
}
