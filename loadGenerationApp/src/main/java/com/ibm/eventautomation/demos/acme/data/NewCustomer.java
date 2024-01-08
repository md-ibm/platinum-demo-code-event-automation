package com.ibm.eventautomation.demos.acme.data;

import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

public class NewCustomer {

    private String timestamp;
    private Customer customer;


    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("customer")
            .field("customerid",   Schema.STRING_SCHEMA)
            .field("customername", Schema.STRING_SCHEMA)
            .field("registered",   Schema.STRING_SCHEMA)
        .build();

    public NewCustomer(String timestamp, Customer customer) {
        this.timestamp = timestamp;
        this.customer = customer;
    }

    public SourceRecord createSourceRecord(String origin) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("customerid"),   customer.getId());
        struct.put(SCHEMA.field("customername"), customer.getName());
        struct.put(SCHEMA.field("registered"),   timestamp);

        return new SourceRecord(createSourcePartition(origin),
                                createSourceOffset(),
                                "CUSTOMERS",
                                Schema.STRING_SCHEMA, customer.getId(),
                                SCHEMA,
                                struct);
    }

    private Map<String, Object> createSourcePartition(String origin) {
        return Collections.singletonMap("partition", origin);
    }
    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }



    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String toString() {
        return "NewCustomer [timestamp=" + timestamp + ", customer=" + customer + "]";
    }
}
