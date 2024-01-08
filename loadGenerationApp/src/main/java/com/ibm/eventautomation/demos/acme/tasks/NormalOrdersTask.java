package com.ibm.eventautomation.demos.acme.tasks;

import java.util.Queue;
import java.util.Timer;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.acme.DatagenSourceConfig;
import com.ibm.eventautomation.demos.acme.data.ReturnsGenerator;
import com.ibm.eventautomation.demos.acme.data.Order;
import com.ibm.eventautomation.demos.acme.data.OrderGenerator;
import com.ibm.eventautomation.demos.acme.utils.Generators;

public class NormalOrdersTask extends DatagenTimerTask {

    private static final String ORIGIN = NormalOrdersTask.class.getName();

    private double cancellationRatio;
    private int cancellationMinDelay;
    private int cancellationMaxDelay;

    private int minItems;
    private int maxItems;


    public NormalOrdersTask(AbstractConfig config,
                            OrderGenerator orderGenerator,
                            ReturnsGenerator cancellationGenerator,
                            Queue<SourceRecord> queue,
                            Timer timer)
    {
        super(orderGenerator, cancellationGenerator, queue, timer);

        cancellationRatio = config.getDouble(DatagenSourceConfig.CONFIG_RETURNS_RATIO);
        cancellationMinDelay = config.getInt(DatagenSourceConfig.CONFIG_RETURNS_MIN_DELAY);
        cancellationMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_RETURNS_MAX_DELAY);

        minItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_SMALL_MIN);
        maxItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_LARGE_MAX);
    }


    @Override
    public void run() {
        Order order = orderGenerator.generate(minItems, maxItems);
        queue.add(order.createSourceRecord(ORIGIN));

        if (Generators.shouldDo(cancellationRatio)) {
            cancelOrder(order, ORIGIN,
                        cancellationMinDelay,
                        cancellationMaxDelay);
        }
    }
}
