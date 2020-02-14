package com.toth.ruler.service

import com.tbot.ruler.exceptions.ServiceException
import com.tbot.ruler.message.DeliveryReport
import com.tbot.ruler.message.Message
import com.tbot.ruler.message.payloads.BooleanTogglePayload
import com.tbot.ruler.message.payloads.BooleanUpdatePayload
import com.tbot.ruler.service.DeliveryReportListenerService
import com.tbot.ruler.things.EmitterId
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class DeliveryReportListenerServiceSpec extends Specification {

    def msg1 = Message.builder().senderId(new EmitterId("123")).payload(BooleanUpdatePayload.UPDATE_TRUE).build();
    def msg2 = Message.builder().senderId(new EmitterId("234")).payload(BooleanUpdatePayload.UPDATE_FALSE).build();
    def msg3 = Message.builder().senderId(new EmitterId("345")).payload(BooleanTogglePayload.TOGGLE_PAYLOAD).build();

    def "single waiter per message id"() {
        given:
        def service = new DeliveryReportListenerService();

        when:
        service.settleFuture(msg1.id);
        service.settleFuture(msg2.id);

        then:
        service.taskMap.get(msg1.id).getClass() == CompletableFuture.class;
        service.taskMap.get(msg2.id).getClass() == CompletableFuture.class;
    }

    def "waiters for mixed message ids"() {
        given:
        def service = new DeliveryReportListenerService();

        when:
        service.settleFuture(msg1.id);
        service.settleFuture(msg2.id);
        service.settleFuture(msg2.id);
        service.settleFuture(msg3.id);
        service.settleFuture(msg3.id);
        service.settleFuture(msg3.id);

        then:
        CompletableFuture.isAssignableFrom(service.taskMap.get(msg1.id).getClass());
        Set.isAssignableFrom(service.taskMap.get(msg2.id).getClass());
        ((Set) service.taskMap.get(msg2.id)).size() == 2;
        Set.isAssignableFrom(service.taskMap.get(msg3.id).getClass());
        ((Set) service.taskMap.get(msg3.id)).size() == 3;
    }

    def "map key empty after removing all waiters"() {
        given:
        def service = new DeliveryReportListenerService();

        when:
        def future1 = service.settleFuture(msg1.id);
        def future2 = service.settleFuture(msg2.id);
        def future3 = service.settleFuture(msg2.id);

        service.removeFuture(msg1.id, future1);
        service.removeFuture(msg2.id, future2);
        service.removeFuture(msg2.id, future3);

        then:
        service.taskMap.get(msg1.id) == null;
        service.taskMap.get(msg2.id) == null;
    }

    def "map key empty after delivering message"() {
        given:
        def service = new DeliveryReportListenerService();

        when:
        service.settleFuture(msg1.id);
        service.settleFuture(msg2.id);
        service.settleFuture(msg2.id);

        service.acceptDeliveryReport(DeliveryReport.builder().originalMessage(msg1).build());
        service.acceptDeliveryReport(DeliveryReport.builder().originalMessage(msg2).build());

        then:
        service.taskMap.get(msg1.id) == null;
        service.taskMap.get(msg2.id) == null;
    }

    def "waiting time outs after one millisecond"() {
        given:
        def service = new DeliveryReportListenerService();

        when:
        service.deliverAndWaitForReport(333, {}, 1);

        then:
        thrown(ServiceException);
    }

    def "waiting time outs after one millisecond using waiter thread"() {
        given:
        def messageId = 123;
        def service = new DeliveryReportListenerService();

        when:
        def waiter = hangOnWaiter(messageId, service, 1);
        sleep(50);

        then:
        waiter.failed;
    }

    def "waiter receives report"() {
        given:
        def service = new DeliveryReportListenerService();
        def report = DeliveryReport.builder().originalMessage(msg1).build();

        when:
        def waiter = hangOnWaiter(msg1.id, service, 60_000);
        service.acceptDeliveryReport(report);

        then:
        waiter.failed == false;
        waiter.receivedReport == report;
    }

    def hangOnWaiter(long messageId, DeliveryReportListenerService service, long timeout) {
        def lock = new Semaphore(1);
        def waiter = new Waiter();
        waiter.messageId = messageId;
        waiter.timeout = timeout;
        waiter.service = service;
        waiter.lock = lock;
        lock.acquire();
        new Thread(waiter).start();
        lock.tryAcquire(1, TimeUnit.SECONDS);
        return waiter;
    }

    class Waiter implements Runnable {

        long messageId;
        long timeout;
        DeliveryReportListenerService service;
        Semaphore lock;

        DeliveryReport receivedReport;
        boolean failed;

        @Override
        public void run() {
            try {
                this.receivedReport = service.deliverAndWaitForReport(messageId, { lock.release() }, timeout);
            } catch(ServiceException e) {
                e.getCause().printStackTrace();
                this.failed = true;
            }
        }
    }
}
