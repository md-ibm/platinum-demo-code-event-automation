package com.ibm.eventautomation.demos.acme.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.acme.DatagenSourceConfig;
import com.ibm.eventautomation.demos.acme.utils.Generators;

public class InventoryGenerator {

    private final List<String> warehouses;

    private ProductGenerator productDescriptionGenerator;

    private final DateTimeFormatter timestampFormatter;


    public InventoryGenerator(AbstractConfig config)
    {
        this.productDescriptionGenerator = new ProductGenerator(config);

        this.warehouses = config.getList(DatagenSourceConfig.CONFIG_LOCATIONS_WAREHOUSES);
        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));
    }


    public Inventory generate() {
        int quantity = Generators.randomInt(20, 500);

        return new Inventory(UUID.randomUUID().toString(),
                                 timestampFormatter.format(LocalDateTime.now()),
                                 Generators.randomItem(warehouses),
                                 productDescriptionGenerator.generate(),
                                 quantity - (quantity % 10));
    }
}
