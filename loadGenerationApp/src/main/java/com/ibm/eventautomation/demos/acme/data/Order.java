package com.ibm.eventautomation.demos.acme.data;

import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.acme.utils.SendToMQ;

public class Order {

    private String id;
    private String timestamp;
    private Customer customer;
    private String description;
    private double unitPrice;
    private int quantity;
    private String region;

    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("order")
            .field("id",          Schema.STRING_SCHEMA)
            .field("customer",    Schema.STRING_SCHEMA)
            .field("customerid",  Schema.STRING_SCHEMA)
            .field("description", Schema.STRING_SCHEMA)
            .field("price",       Schema.FLOAT64_SCHEMA)
            .field("quantity",    Schema.INT32_SCHEMA)
            .field("region",      Schema.STRING_SCHEMA)
            .field("timestamp",   Schema.STRING_SCHEMA)
        .build();


    public Order(String id, String timestamp, Customer customer, String description, double unitPrice, int quantity, String region) {
        this.id = id;
        this.timestamp = timestamp;
        this.customer = customer;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.region = region;
    }


    public SourceRecord createSourceRecord(String origin) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("id"),          id);
        struct.put(SCHEMA.field("customer"),    customer.getName());
        struct.put(SCHEMA.field("customerid"),  customer.getId());
        struct.put(SCHEMA.field("description"), description);
        struct.put(SCHEMA.field("price"),       unitPrice);
        struct.put(SCHEMA.field("quantity"),    quantity);
        struct.put(SCHEMA.field("region"),      region);
        struct.put(SCHEMA.field("timestamp"),   timestamp);

        SendToMQ.send(struct);
        return new SourceRecord(createSourcePartition(origin),
                                createSourceOffset(),
                                "PRODUCTS",
                                Schema.STRING_SCHEMA, id,
                                SCHEMA,
                                struct);
    }

    private Map<String, Object> createSourcePartition(String origin) {
        return Collections.singletonMap("partition", origin);
    }
    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }


    public String getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public String getRegion() {
        return region;
    }
    public Customer getCustomer() {
        return customer;
    }


    @Override
    public String toString() {
        return "Order [id=" + id + ", timestamp=" + timestamp + ", customer=" + customer + ", description="
                + description + ", unitPrice=" + unitPrice + ", quantity=" + quantity + ", region=" + region + "]";
    }
}
