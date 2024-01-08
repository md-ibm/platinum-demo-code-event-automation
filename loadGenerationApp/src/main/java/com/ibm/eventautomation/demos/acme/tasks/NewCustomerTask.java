package com.ibm.eventautomation.demos.acme.tasks;

import java.util.Queue;
import java.util.Timer;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.acme.DatagenSourceConfig;
import com.ibm.eventautomation.demos.acme.data.NewCustomer;
import com.ibm.eventautomation.demos.acme.data.NewCustomerGenerator;
import com.ibm.eventautomation.demos.acme.data.OrderGenerator;
import com.ibm.eventautomation.demos.acme.utils.Generators;

public class NewCustomerTask extends DatagenTimerTask {

    private static final String ORIGIN = NewCustomerTask.class.getName();

    private double firstOrderRatio;
    private int firstOrderMinDelay;
    private int firstOrderMaxDelay;

    private NewCustomerGenerator generator;




    public NewCustomerTask(AbstractConfig config,
                           OrderGenerator orderGenerator,
                           Queue<SourceRecord> queue,
                           Timer timer)
    {
        super(orderGenerator, queue, timer);

        generator = new NewCustomerGenerator(config);

        firstOrderRatio = config.getDouble(DatagenSourceConfig.CONFIG_NEWCUSTOMERS_ORDER_RATIO);
        firstOrderMinDelay = config.getInt(DatagenSourceConfig.CONFIG_NEWCUSTOMERS_ORDER_MIN_DELAY);
        firstOrderMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_NEWCUSTOMERS_ORDER_MAX_DELAY);
    }


    @Override
    public void run() {
        NewCustomer newCustomer = generator.generate();
        queue.add(newCustomer.createSourceRecord(ORIGIN));

        if (Generators.shouldDo(firstOrderRatio)) {
            int orderDelay = Generators.randomInt(firstOrderMinDelay, firstOrderMaxDelay);

            scheduleOrder(ORIGIN,
                          orderDelay,
                          newCustomer.getCustomer());
        }
    }

}
