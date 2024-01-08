package com.ibm.eventautomation.demos.acme.data;

import java.util.List;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.acme.DatagenSourceConfig;
import com.ibm.eventautomation.demos.acme.utils.Generators;

public class ReturnsGenerator {

    private final List<String> reasons;

    private String timestampFormat;


    public ReturnsGenerator(AbstractConfig config) {
        this.reasons = config.getList(DatagenSourceConfig.CONFIG_RETURNS_REASONS);
        this.timestampFormat = config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS);
    }

    public Returns generate(Order order) {
        String reason = Generators.randomItem(reasons);
        return new Returns(order, reason, timestampFormat);
    }
}
