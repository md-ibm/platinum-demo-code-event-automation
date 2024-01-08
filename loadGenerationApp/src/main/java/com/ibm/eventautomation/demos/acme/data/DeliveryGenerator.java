package com.ibm.eventautomation.demos.acme.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.kafka.common.config.AbstractConfig;

import com.github.javafaker.Faker;
import com.ibm.eventautomation.demos.acme.DatagenSourceConfig;
import com.ibm.eventautomation.demos.acme.utils.Generators;

public class DeliveryGenerator {

    private final DateTimeFormatter timestampFormatter;

    private final Faker faker;


    public DeliveryGenerator(AbstractConfig config)
    {
        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));
        this.faker = new Faker();
    }


    // generate an order with known details
    public Delivery generate(Customer customer)
    {
        if (customer == null) {
            customer = new Customer(faker);
        }

        return new Delivery(UUID.randomUUID().toString(),
                         timestampFormatter.format(LocalDateTime.now()),
                         timestampFormatter.format(LocalDateTime.now().plusDays(1).withHour(Generators.randomInt(9, 18)).withMinute(0).withSecond(0)),
                         customer,
                         UUID.randomUUID().toString(),
                         faker);
    }
}