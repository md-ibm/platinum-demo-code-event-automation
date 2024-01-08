package com.ibm.eventautomation.demos.acme.tasks;

import java.util.Queue;
import java.util.TimerTask;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.acme.data.InventoryGenerator;

public class InventoryTask extends TimerTask {

    private InventoryGenerator generator;
    private Queue<SourceRecord> queue;

    public InventoryTask(AbstractConfig config, Queue<SourceRecord> queue) {
        this.generator = new InventoryGenerator(config);
        this.queue = queue;
    }

    @Override
    public void run() {
        queue.add(generator.generate().createSourceRecord());
    }
}
