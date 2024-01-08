package com.ibm.eventautomation.demos.acme.tasks;

import java.util.Queue;
import java.util.TimerTask;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.acme.data.DeliveryGenerator;

public class DeliveryTask extends TimerTask {

    private DeliveryGenerator generator;
    private Queue<SourceRecord> queue;
    private static final String ORIGIN = DeliveryTask.class.getName();

    public DeliveryTask(AbstractConfig config, Queue<SourceRecord> queue) {
        this.generator = new DeliveryGenerator(config);
        this.queue = queue;
    }

    @Override
    public void run() {
        queue.add(generator.generate(null).createSourceRecord(ORIGIN));
    }
}
