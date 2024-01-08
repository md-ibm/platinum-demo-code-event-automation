package com.ibm.eventautomation.demos.acme.data;

import java.util.List;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.acme.DatagenSourceConfig;
import com.ibm.eventautomation.demos.acme.utils.Generators;

public class ProductGenerator {

    private final List<String> sizes;
    private final List<String> materials;
    private final List<String> length;

    private final String product;


    public ProductGenerator(AbstractConfig config)
    {
        this.sizes = config.getList(DatagenSourceConfig.CONFIG_PRODUCTS_SIZES);
        this.materials = config.getList(DatagenSourceConfig.CONFIG_PRODUCTS_MATERIALS);
        this.length = config.getList(DatagenSourceConfig.CONFIG_RACEQUET_LENGTH);
        this.product = config.getString(DatagenSourceConfig.CONFIG_PRODUCTS_NAME);
    }

    public String generate() {
        return Generators.randomItem(materials) + " " +
               Generators.randomItem(sizes) + " " +
               Generators.randomItem(length) + "in " +
               product;
    }
}
