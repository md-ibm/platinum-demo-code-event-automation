package com.ibm.eventautomation.demos.acme.data;

import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import com.github.javafaker.Faker;

public class Delivery {

    private String trackingId;
    private String estimatedDateTime;
    private String timestamp;
    private String customerName;
    private String orderId;
    private String addressStreet;
    private String addressLocality;
    private String addressRegion;
    private String addressCountry;
    private String postalCode;

    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("delivery")
            .field("trackingId",            Schema.STRING_SCHEMA)
            .field("estimatedDateTime",     Schema.STRING_SCHEMA)
            .field("timestamp",             Schema.STRING_SCHEMA)
            .field("orderId",               Schema.STRING_SCHEMA)
            .field("customerName",          Schema.STRING_SCHEMA)
            .field("addressStreet",         Schema.STRING_SCHEMA)
            .field("addressLocality",       Schema.STRING_SCHEMA)
            .field("addressRegion",         Schema.STRING_SCHEMA)
            .field("addressCountry",        Schema.STRING_SCHEMA)
            .field("postalCode",            Schema.STRING_SCHEMA)
        .build();


    public Delivery(String trackingId, String timestamp, String estimatedDateTime, Customer customer, String orderId, Faker faker) {
        this.trackingId = trackingId;
        this.estimatedDateTime = estimatedDateTime;
        this.timestamp = timestamp;
        this.customerName = customer.getName();
        this.orderId = orderId;
        this.addressStreet = faker.address().streetAddress();
        this.addressLocality = faker.address().city();
        this.addressRegion = faker.address().state();
        this.addressCountry = "USA";
        this.postalCode = faker.address().zipCode();
    }


    public SourceRecord createSourceRecord(String origin) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("trackingId"),          trackingId);
        struct.put(SCHEMA.field("estimatedDateTime"),    estimatedDateTime);
        struct.put(SCHEMA.field("timestamp"),  timestamp);
        struct.put(SCHEMA.field("orderId"), orderId);
        struct.put(SCHEMA.field("customerName"),       customerName);
        struct.put(SCHEMA.field("addressStreet"),    addressStreet);
        struct.put(SCHEMA.field("addressLocality"),      addressLocality);
        struct.put(SCHEMA.field("addressRegion"),   addressRegion);
        struct.put(SCHEMA.field("addressCountry"),   addressCountry);
        struct.put(SCHEMA.field("postalCode"),   postalCode);

        return new SourceRecord(createSourcePartition(origin),
                                createSourceOffset(),
                                "DELIVERY",
                                Schema.STRING_SCHEMA, trackingId,
                                SCHEMA,
                                struct);
    }

    private Map<String, Object> createSourcePartition(String origin) {
        return Collections.singletonMap("partition", origin);
    }
    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }

    public String getTrackingId() 
    {
        return trackingId;
    }

    public String getEstimatedDateTime() {
        return estimatedDateTime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public String getAddressLocality() {
        return addressLocality;
    }

    public String getAddressRegion() {
        return addressRegion;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public String toString() {
        return "Delivery [trackingId=" + trackingId + ", timestamp=" + timestamp + ", estimatedDateTime=" + estimatedDateTime + ", customerName="
                + customerName + ", orderId=" + orderId + ", addressStreet=" + addressStreet + ", addressLocality=" + addressLocality 
                + ", addressLocality=" + addressLocality + ", addressRegion=" + addressRegion+ ", postalCode=" + postalCode + "]";
    }

}
