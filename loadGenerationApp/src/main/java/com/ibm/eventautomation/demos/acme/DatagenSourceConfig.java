package com.ibm.eventautomation.demos.acme;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.NonEmptyString;
import org.apache.kafka.common.config.ConfigDef.Range;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Validator;
import org.apache.kafka.common.config.ConfigDef.Width;
import org.apache.kafka.common.config.ConfigException;

public class DatagenSourceConfig {

    private static final String CONFIG_GROUP_FORMATS = "Formats";
    public static final String CONFIG_FORMATS_TIMESTAMPS = "formats.timestamps";

    private static final String CONFIG_GROUP_LOCATIONS = "Locations";
    public static final String CONFIG_LOCATIONS_REGIONS = "locations.regions";
    public static final String CONFIG_LOCATIONS_WAREHOUSES = "locations.warehouses";

    private static final String CONFIG_GROUP_PRODUCTS = "Products";
    public static final String CONFIG_PRODUCTS_SIZES     = "products.sizes";
    public static final String CONFIG_PRODUCTS_MATERIALS = "products.materials";
    public static final String CONFIG_RACEQUET_LENGTH    = "products.length";
    public static final String CONFIG_PRODUCTS_NAME      = "products.name";

    private static final String CONFIG_GROUP_DYNAMICPRICING = "Dynamic pricing";
    public static final String CONFIG_PRODUCTS_MIN_PRICE              = "prices.min";
    public static final String CONFIG_PRODUCTS_MAX_PRICE              = "prices.max";
    public static final String CONFIG_DYNAMICPRICING_PRICE_CHANGE_MAX = "prices.maxvariation";

    private static final String CONFIG_GROUP_ORDERS = "Orders";
    public static final String CONFIG_ORDERS_SMALL_MIN = "orders.small.quantity.min";
    public static final String CONFIG_ORDERS_SMALL_MAX = "orders.small.quantity.max";
    public static final String CONFIG_ORDERS_LARGE_MIN = "orders.large.quantity.min";
    public static final String CONFIG_ORDERS_LARGE_MAX = "orders.large.quantity.max";

    private static final String CONFIG_GROUP_RETURNS = "Returns";
    public static final String CONFIG_RETURNS_RATIO     = "returns.ratio";
    public static final String CONFIG_RETURNS_MIN_DELAY = "returns.delay.ms.min";
    public static final String CONFIG_RETURNS_MAX_DELAY = "returns.delay.ms.max";
    public static final String CONFIG_RETURNS_REASONS   = "returns.reasons";

    private static final String CONFIG_GROUP_NEWCUSTOMERS = "New customers";
    public static final String CONFIG_NEWCUSTOMERS_ORDER_RATIO     = "newcustomers.order.ratio";
    public static final String CONFIG_NEWCUSTOMERS_ORDER_MIN_DELAY = "newcustomers.order.delay.ms.min";
    public static final String CONFIG_NEWCUSTOMERS_ORDER_MAX_DELAY = "newcustomers.order.delay.ms.max";

    private static final String CONFIG_GROUP_TIMES = "Timings";
    public static final String CONFIG_TIMES_ORDERS           = "timings.ms.orders";
    public static final String CONFIG_TIMES_INVENTORY   = "timings.ms.inventory";
    public static final String CONFIG_TIMES_BADGEINS         = "timings.ms.badgeins";
    public static final String CONFIG_TIMES_NEWCUSTOMERS     = "timings.ms.newcustomers";
    public static final String CONFIG_TIMES_DELIVERY         = "timings.ms.delivery";


    public static final ConfigDef CONFIG_DEF = new ConfigDef()
        //
        // format to use
        //
        .define(CONFIG_FORMATS_TIMESTAMPS,
                    Type.STRING,
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Format to use for timestamps generated for events.",
                    CONFIG_GROUP_FORMATS, 1, Width.LONG, "Timestamp format")
        //
        // how to generate locations
        //
        .define(CONFIG_LOCATIONS_REGIONS,
                    Type.LIST,
                    Arrays.asList("NA", "SA", "EMEA", "APAC", "ANZ"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of regions to use for generated locations. Regions cannot contain spaces.",
                    CONFIG_GROUP_LOCATIONS, 1, Width.MEDIUM, "Regions")
        .define(CONFIG_LOCATIONS_WAREHOUSES,
                    Type.LIST,
                    Arrays.asList("North", "South", "West", "East", "Central"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of warehouses to use for generated locations. Warehouse names cannot contain spaces.",
                    CONFIG_GROUP_LOCATIONS, 2, Width.MEDIUM, "Regions")
        //
        // How to generate product names
        //
        .define(CONFIG_PRODUCTS_SIZES,
                    Type.LIST,
                    Arrays.asList("Mid-size", "Normal", "Mid-plus", "Oversize"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of sizes to use for generated product names. Sizes cannot contain spaces.",
                    CONFIG_GROUP_PRODUCTS, 1, Width.MEDIUM, "Sizes")
        .define(CONFIG_PRODUCTS_MATERIALS,
                    Type.LIST,
                    Arrays.asList("Graphite", "Fiberglass", "Aluminum", "Composite"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of materials to use for generated product names. Materials cannot contain spaces.",
                    CONFIG_GROUP_PRODUCTS, 2, Width.MEDIUM, "Materials")
        .define(CONFIG_RACEQUET_LENGTH,
                    Type.LIST,
                    Arrays.asList("19", "21", "23", "25", "26", "27",
                                  "28", "29"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of styles to use for generated product names. Styles cannot contain spaces.",
                    CONFIG_GROUP_PRODUCTS, 3, Width.MEDIUM, "Styles")
        .define(CONFIG_PRODUCTS_NAME,
                    Type.STRING,
                    "Tennis Racket",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the product to use for generated product names.",
                    CONFIG_GROUP_PRODUCTS, 4, Width.MEDIUM, "Name")
        //
        // Dynamic prices to generate
        //
        .define(CONFIG_PRODUCTS_MIN_PRICE,
                    Type.DOUBLE,
                    104.99,
                    Range.atLeast(100.01),
                    Importance.LOW,
                    "Minimum price for product orders. Must be greater than 0",
                    CONFIG_GROUP_DYNAMICPRICING, 1, Width.SHORT, "Min price")
        .define(CONFIG_PRODUCTS_MAX_PRICE,
                    Type.DOUBLE,
                    209.99,
                    Range.atLeast(100.01),
                    Importance.LOW,
                    "Maximum price for product orders. Must be greater than 0",
                    CONFIG_GROUP_DYNAMICPRICING, 2, Width.SHORT, "Max price")
        .define(CONFIG_DYNAMICPRICING_PRICE_CHANGE_MAX,
                    Type.DOUBLE,
                    9.99,
                    Range.atLeast(0.01),
                    Importance.LOW,
                    "Maximum price variation caused by dynamic pricing. Must be greater than 0",
                    CONFIG_GROUP_DYNAMICPRICING, 3, Width.SHORT, "Dynamic pricing variation limit")
        //
        // Orders to generate
        //
        .define(CONFIG_ORDERS_SMALL_MIN,
                    Type.INT,
                    1,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Minimum number of items in a small order. Must be greater than 0",
                    CONFIG_GROUP_ORDERS, 1, Width.SHORT, "Min quantity (small orders)")
        .define(CONFIG_ORDERS_SMALL_MAX,
                    Type.INT,
                    10,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Maximum number of items in a small order. Must be greater than 0",
                    CONFIG_GROUP_ORDERS, 2, Width.SHORT, "Max quantity (small orders)")
        .define(CONFIG_ORDERS_LARGE_MIN,
                    Type.INT,
                    5,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Minimum number of items in a large order. Must be greater than 0",
                    CONFIG_GROUP_ORDERS, 3, Width.SHORT, "Min quantity (large orders)")
        .define(CONFIG_ORDERS_LARGE_MAX,
                    Type.INT,
                    15,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Maximum number of items in a large order. Must be greater than 0",
                    CONFIG_GROUP_ORDERS, 4, Width.SHORT, "Max quantity (large orders)")
        //
        // Order returns to generate
        //
        .define(CONFIG_RETURNS_RATIO,
                    Type.DOUBLE,
                    0.005,
                    Range.between(0.001, 1),
                    Importance.LOW,
                    "Ratio of orders that are normally cancelled. Must be between 0 and 1",
                    CONFIG_GROUP_RETURNS, 1, Width.SHORT, "Returns ratio")
        .define(CONFIG_RETURNS_MIN_DELAY,
                    Type.INT,
                    300_000, // 5 minutes
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Minimum delay before an order should normally be cancelled, in milliseconds. Must be at least 60000.",
                    CONFIG_GROUP_RETURNS, 2, Width.SHORT, "Min returns delay")
        .define(CONFIG_RETURNS_MAX_DELAY,
                    Type.INT,
                    7_200_000, // 2 hours
                    Range.atLeast(120_000), // 2 minutes
                    Importance.LOW,
                    "Maximum delay before an order should normally be cancelled, in milliseconds. Must be at least 120000.",
                    CONFIG_GROUP_RETURNS, 3, Width.SHORT, "Max returns delay")
        .define(CONFIG_RETURNS_REASONS,
                    Type.LIST,
                    Arrays.asList("CHANGEDMIND", "BADFIT"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of reasons to use for generated returns. Reasons cannot contain spaces.",
                    CONFIG_GROUP_RETURNS, 4, Width.SHORT, "Returns reason codes")
        //
        // generating new-customer registration events
        //
        .define(CONFIG_NEWCUSTOMERS_ORDER_RATIO,
                    Type.DOUBLE,
                    0.22,
                    Range.between(0.001, 1),
                    Importance.LOW,
                    "Ratio of new customers that should make an order soon after registering. Must be between 0 and 1",
                    CONFIG_GROUP_NEWCUSTOMERS, 1, Width.SHORT, "New customers immediate-order ratio")
        .define(CONFIG_NEWCUSTOMERS_ORDER_MIN_DELAY,
                    Type.INT,
                    180_000, // 3 minutes
                    Range.atLeast(10_000), // 10 seconds
                    Importance.LOW,
                    "Minimum delay before a new customer (who is going to make an immediate order) should create their first order, in milliseconds. Must be at least 10000.",
                    CONFIG_GROUP_NEWCUSTOMERS, 2, Width.SHORT, "New customers immediate-order min delay")
        .define(CONFIG_NEWCUSTOMERS_ORDER_MAX_DELAY,
                    Type.INT,
                    1_380_000, // 23 minutes
                    Range.atLeast(30_000), // 30 seconds
                    Importance.LOW,
                    "Maximum delay before a new customer (who is going to make an immediate order) should create their first order, in milliseconds. Must be at least 30000.",
                    CONFIG_GROUP_NEWCUSTOMERS, 3, Width.SHORT, "New customers immediate-order max delay")
        //
        // How frequently to generate messages
        //
        .define(CONFIG_TIMES_ORDERS,
                    Type.INT,
                    30_000, // 30 seconds
                    Range.atLeast(500),
                    Importance.LOW,
                    "Delay, in milliseconds, between each order that should be generated.",
                    CONFIG_GROUP_TIMES, 1, Width.MEDIUM, "Orders delay")
        .define(CONFIG_TIMES_DELIVERY,
                    Type.INT,
                    30_000, // 30 seconds
                    Range.atLeast(500),
                    Importance.LOW,
                    "Delay, in milliseconds, between each delivery that should be generated.",
                    CONFIG_GROUP_TIMES, 1, Width.MEDIUM, "Delivery delay")
        .define(CONFIG_TIMES_INVENTORY,
                    Type.INT,
                    300_000, // 5 minutes
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Delay, in milliseconds, between each stock movement event that should be generated.",
                    CONFIG_GROUP_TIMES, 4, Width.MEDIUM, "Stock movements delay")
        .define(CONFIG_TIMES_BADGEINS,
                    Type.INT,
                    600,
                    Range.atLeast(500),
                    Importance.LOW,
                    "Delay, in milliseconds, between each badge in event that should be generated.",
                    CONFIG_GROUP_TIMES, 4, Width.MEDIUM, "Stock movements delay")
        .define(CONFIG_TIMES_NEWCUSTOMERS,
                    Type.INT,
                    543_400, // a little over 9 minutes
                    Range.atLeast(5_000),  // 5 seconds
                    Importance.LOW,
                    "Delay, in milliseconds, between each new customer that should be generated.");




    private static class ValidTermsList implements Validator {
        @Override
        public void ensureValid(final String name, final Object value) {
            @SuppressWarnings("unchecked")
            List<String> values = (List<String>) value;
            if (values.isEmpty()) {
                throw new ConfigException(name, value, "must contain at least one item.");
            }
            for (String item : values) {
                if (item.contains(" ")) {
                    throw new ConfigException(name, item, "must not contain items with spaces.");
                }
            }
        }

        @Override
        public String toString() {
            return "List containing at least one element, where all elements don't have a space.";
        }
    }
}
