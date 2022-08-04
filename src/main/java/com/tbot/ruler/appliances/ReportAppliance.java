package com.tbot.ruler.appliances;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.ReportPayload;
import com.tbot.ruler.model.ReportEntry;
import com.tbot.ruler.service.PersistenceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class ReportAppliance extends AbstractAppliance<List<ReportEntry>> {

    private final int reportSize = 20;
    private final Semaphore lock = new Semaphore(1);
    private ConcurrentLinkedQueue<ReportEntry> reportEntries;

    public ReportAppliance(String id, PersistenceService persistenceService) {
        super(id, persistenceService);
        this.reportEntries = new ConcurrentLinkedQueue<>();
    }

    @Override
    public Optional<List<ReportEntry>> getState() {
        return Optional.of(new ArrayList<>(reportEntries));
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload payload) {
        return Optional.empty();
    }

    @Override
    public void acceptMessage(Message message) {
        ReportPayload payload = message.getPayload().ensureMessageType();
        pushEntry(payload.getReportEntry());
    }

    public void pushEntry(ReportEntry reportEntry) {
        lock.acquireUninterruptibly();
        try {
            while (reportEntries.size() >= reportSize) {
                reportEntries.poll();
            }
            reportEntries.add(reportEntry);
        } finally {
            lock.release();
        }
    }
}
