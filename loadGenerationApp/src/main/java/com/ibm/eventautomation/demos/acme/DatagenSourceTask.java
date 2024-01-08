package com.ibm.eventautomation.demos.acme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.acme.data.ReturnsGenerator;
import com.ibm.eventautomation.demos.acme.data.OrderGenerator;
import com.ibm.eventautomation.demos.acme.tasks.NewCustomerTask;
import com.ibm.eventautomation.demos.acme.tasks.NormalOrdersTask;
import com.ibm.eventautomation.demos.acme.tasks.DeliveryTask;
import com.ibm.eventautomation.demos.acme.tasks.InventoryTask;

public class DatagenSourceTask extends SourceTask {

    private static Logger log = LoggerFactory.getLogger(DatagenSourceTask.class);

    private OrderGenerator orderGenerator;
    private ReturnsGenerator returnsGenerator;

    private Timer generateTimer = new Timer();
    private Queue<SourceRecord> queue = new ConcurrentLinkedQueue<>();




    @Override
    public void start(Map<String, String> props) {
        log.info("Starting task {}", props);

        AbstractConfig config = new AbstractConfig(DatagenSourceConfig.CONFIG_DEF, props);
        orderGenerator = new OrderGenerator(config);
        returnsGenerator = new ReturnsGenerator(config);

        NormalOrdersTask normalOrders = new NormalOrdersTask(config,
                orderGenerator, returnsGenerator,
                queue, generateTimer);
        InventoryTask inventory = new InventoryTask(config, queue);
        NewCustomerTask newCustomers = new NewCustomerTask(config,
                orderGenerator,
                queue, generateTimer);
        DeliveryTask delivery = new DeliveryTask(config, queue);

        generateTimer.scheduleAtFixedRate(normalOrders,        0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_ORDERS));
        generateTimer.scheduleAtFixedRate(inventory,      0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_INVENTORY));
        generateTimer.scheduleAtFixedRate(newCustomers,        0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_NEWCUSTOMERS));
        generateTimer.scheduleAtFixedRate(delivery,        0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_DELIVERY));
    }


    @Override
    public void stop() {
        log.info("Stopping task");

        generateTimer.cancel();
        queue.clear();
    }


    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> currentRecords = new ArrayList<>();

        SourceRecord nextItem = queue.poll();
        while (nextItem != null) {
            currentRecords.add(nextItem);

            nextItem = queue.poll();
        }
        return currentRecords;
    }


    @Override
    public String version() {
        return DatagenSourceConnector.VERSION;
    }
}
