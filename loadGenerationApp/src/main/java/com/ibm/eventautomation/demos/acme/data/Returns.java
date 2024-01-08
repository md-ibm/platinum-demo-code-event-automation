package com.ibm.eventautomation.demos.acme.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

public class Returns {

    private String id;
    private Order order;
    private String reason;
    private String timestampFormat;

    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("returns")
            .field("id",        Schema.STRING_SCHEMA)
            .field("orderid",   Schema.STRING_SCHEMA)
            .field("timestamp", Schema.STRING_SCHEMA)
            .field("reason",    Schema.STRING_SCHEMA)
        .build();


    public Returns(Order order, String reason, String timestampFormat) {
        this.order = order;
        this.id = UUID.randomUUID().toString();
        this.reason = reason;
        this.timestampFormat = timestampFormat;
    }


    public SourceRecord createSourceRecord(String origin) {
        String timestamp = DateTimeFormatter.ofPattern(timestampFormat).format(LocalDateTime.now());

        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("id"),        id);
        struct.put(SCHEMA.field("orderid"),   order.getId());
        struct.put(SCHEMA.field("timestamp"), timestamp);
        struct.put(SCHEMA.field("reason"),    reason);

        return new SourceRecord(createSourcePartition(origin),
                                createSourceOffset(timestamp),
                                "RETURNS",
                                Schema.STRING_SCHEMA, id,
                                SCHEMA,
                                struct);
    }

    private Map<String, Object> createSourcePartition(String origin) {
        return Collections.singletonMap("partition", origin);
    }
    private Map<String, Object> createSourceOffset(String timestamp) {
        return Collections.singletonMap("offset", timestamp);
    }

}
