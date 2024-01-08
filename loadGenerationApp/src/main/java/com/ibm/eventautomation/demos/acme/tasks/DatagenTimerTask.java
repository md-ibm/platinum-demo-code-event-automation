package com.ibm.eventautomation.demos.acme.tasks;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.acme.data.ReturnsGenerator;
import com.ibm.eventautomation.demos.acme.data.Customer;
import com.ibm.eventautomation.demos.acme.data.Order;
import com.ibm.eventautomation.demos.acme.data.OrderGenerator;
import com.ibm.eventautomation.demos.acme.utils.Generators;

public abstract class DatagenTimerTask extends TimerTask {

    private final Logger log = LoggerFactory.getLogger(DatagenTimerTask.class);

    protected final OrderGenerator orderGenerator;
    protected final ReturnsGenerator returnsGenerator;
    protected final Queue<SourceRecord> queue;
    private final Timer timer;

    protected DatagenTimerTask(OrderGenerator orderGenerator,
                               ReturnsGenerator returnsGenerator,
                               Queue<SourceRecord> queue,
                               Timer generateTimer)
    {
        this.orderGenerator = orderGenerator;
        this.returnsGenerator = returnsGenerator;
        this.queue = queue;
        this.timer = generateTimer;
    }

    protected DatagenTimerTask(OrderGenerator orderGenerator,
                               Queue<SourceRecord> queue,
                               Timer generateTimer)
    {
        this.orderGenerator = orderGenerator;
        this.returnsGenerator = null;
        this.queue = queue;
        this.timer = generateTimer;
    }



    protected void cancelOrder(final Order order, final String origin, final int minDelay, final int maxDelay) {
        if (this.returnsGenerator == null) {
            log.error("Attempting to cancel order without providing a return generator");
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                queue.add(returnsGenerator.generate(order)
                            .createSourceRecord(origin));
            }
        }, Generators.randomInt(minDelay, maxDelay));
    }


    protected void scheduleOrder(final String origin, final int delay,
                                 Customer customer)
    {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Order order = orderGenerator.generate(customer);
                queue.add(order.createSourceRecord(origin));
            }
        }, delay);
    }



    protected void scheduleOrder(final String origin, final int delay,
            Customer customer,
            int minNumItems, int maxNumItems,
            double unitPrice,
            String region,
            String productDescription)
    {
        final Integer cancelDelayMin = null;
        final Integer cancelDelayMax = null;

        scheduleOrder(origin, delay,
                      customer,
                      minNumItems, maxNumItems,
                      unitPrice,
                      region,
                      productDescription,
                      cancelDelayMin, cancelDelayMax);
    }

    protected void scheduleOrder(final String origin, final int delay,
        Customer customer,
        int minNumItems, int maxNumItems,
        double unitPrice,
        String region,
        String productDescription,
        Integer cancelDelayMin, Integer cancelDelayMax)
    {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Order order = orderGenerator.generate(minNumItems, maxNumItems,
                                                      unitPrice,
                                                      region,
                                                      productDescription,
                                                      customer);
                queue.add(order.createSourceRecord(origin));

                if (cancelDelayMin != null && cancelDelayMax != null) {
                    cancelOrder(order, origin, cancelDelayMin, cancelDelayMax);
                }
            }
        }, delay);
    }
}
