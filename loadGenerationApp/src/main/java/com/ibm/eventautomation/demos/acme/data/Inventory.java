package com.ibm.eventautomation.demos.acme.data;

import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

public class Inventory {

    private String movementid;
    private String timestamp;
    private String warehouse;
    private String productDescription;
    private int quantity;

    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("inventory")
            .field("movementid", Schema.STRING_SCHEMA)
            .field("warehouse",  Schema.STRING_SCHEMA)
            .field("product",    Schema.STRING_SCHEMA)
            .field("quantity",   Schema.INT32_SCHEMA)
            .field("timestamp",  Schema.STRING_SCHEMA)
        .build();

    public Inventory(String id, String timestamp, String warehouse, String product, int quantity) {
        this.movementid = id;
        this.timestamp = timestamp;
        this.warehouse = warehouse;
        this.productDescription = product;
        this.quantity = quantity;
    }

    public SourceRecord createSourceRecord() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("movementid"), movementid);
        struct.put(SCHEMA.field("warehouse"), warehouse);
        struct.put(SCHEMA.field("product"), productDescription);
        struct.put(SCHEMA.field("quantity"), quantity);
        struct.put(SCHEMA.field("timestamp"), timestamp);

        return new SourceRecord(createSourcePartition(),
                                createSourceOffset(),
                                "INVENTORY",
                                Schema.STRING_SCHEMA, movementid,
                                SCHEMA,
                                struct);
    }

    private Map<String, Object> createSourcePartition() {
        return Collections.singletonMap("partition", "stock");
    }
    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }
}
