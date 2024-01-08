package com.ibm.eventautomation.demos.acme.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.config.AbstractConfig;

import com.github.javafaker.Faker;
import com.ibm.eventautomation.demos.acme.DatagenSourceConfig;
import com.ibm.eventautomation.demos.acme.utils.Generators;

public class OrderGenerator {

    private final List<String> regions;

    private final double minPrice;
    private final double maxPrice;

    private ProductGenerator productDescriptionGenerator;

    private final DateTimeFormatter timestampFormatter;

    private final Faker faker;


    public OrderGenerator(AbstractConfig config)
    {
        this.productDescriptionGenerator = new ProductGenerator(config);

        this.regions = config.getList(DatagenSourceConfig.CONFIG_LOCATIONS_REGIONS);
        this.minPrice = config.getDouble(DatagenSourceConfig.CONFIG_PRODUCTS_MIN_PRICE);
        this.maxPrice = config.getDouble(DatagenSourceConfig.CONFIG_PRODUCTS_MAX_PRICE);

        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));

        this.faker = new Faker();
    }


    // generate random order
    public Order generate(int minItems, int maxItems) {
        double unitPrice = Generators.randomPrice(minPrice, maxPrice);
        String description = productDescriptionGenerator.generate();
        String region = Generators.randomItem(regions);
        Customer customer = new Customer(faker);

        return generate(minItems, maxItems,
                        unitPrice,
                        region,
                        description,
                        customer);
    }

    // generate an order for a known customer
    public Order generate(Customer customer) {
        int minItems = 1;
        int maxItems = 1;
        double unitPrice = Generators.randomPrice(minPrice, maxPrice);
        String description = productDescriptionGenerator.generate();
        String region = Generators.randomItem(regions);

        return generate(minItems, maxItems,
                        unitPrice,
                        region,
                        description,
                        customer);
    }

    // generate an order with known details
    public Order generate(int minItems, int maxItems,
            double unitPrice,
            String region,
            String description,
            Customer customer)
    {
        int quantity = Generators.randomInt(minItems, maxItems);
        if (customer == null) {
            customer = new Customer(faker);
        }

        return new Order(UUID.randomUUID().toString(),
                         timestampFormatter.format(LocalDateTime.now()),
                         customer,
                         description,
                         unitPrice, quantity,
                         region);
    }
}
