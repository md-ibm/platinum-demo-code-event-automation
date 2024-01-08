package com.ibm.eventautomation.demos.acme.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.kafka.common.config.AbstractConfig;

import com.github.javafaker.Faker;
import com.ibm.eventautomation.demos.acme.DatagenSourceConfig;

public class NewCustomerGenerator {

    private final DateTimeFormatter timestampFormatter;

    private final Faker faker;


    public NewCustomerGenerator(AbstractConfig config) {
        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));

        this.faker = new Faker();
    }

    public NewCustomer generate() {
        return new NewCustomer(timestampFormatter.format(LocalDateTime.now()),
                               new Customer(faker));
    }
}
