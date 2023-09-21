package com.tbot.ruler.appliances;

import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePayload;
import com.tbot.ruler.broker.payload.ReportLog;
import com.tbot.ruler.service.ApplianceStatePersistenceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class ReportAppliance extends AbstractAppliance<List<ReportLog>> {

    private final int reportSize = 20;
    private final Semaphore lock = new Semaphore(1);
    private ConcurrentLinkedQueue<ReportLog> reportLogs;

    public ReportAppliance(String id, String name, ApplianceStatePersistenceService persistenceService) {
        super(id, name, persistenceService);
        this.reportLogs = new ConcurrentLinkedQueue<>();
    }

    @Override
    public Optional<List<ReportLog>> getState() {
        return Optional.of(new ArrayList<>(reportLogs));
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload payload) {
        throw new MessageUnsupportedException("Direct messages unsupported by appliance " + this.getClass());
    }

    @Override
    public void acceptMessage(Message message) {
        ReportLog reportLog = message.getPayloadAs(ReportLog.class);
        pushLog(reportLog);
    }

    public void pushLog(ReportLog reportLog) {
        lock.acquireUninterruptibly();
        try {
            while (reportLogs.size() >= reportSize) {
                reportLogs.poll();
            }
            reportLogs.add(reportLog);
        } finally {
            lock.release();
        }
    }
}
