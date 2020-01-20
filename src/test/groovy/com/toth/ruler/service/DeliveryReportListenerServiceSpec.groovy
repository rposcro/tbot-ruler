package com.toth.ruler.service

import com.tbot.ruler.exceptions.ServiceException
import com.tbot.ruler.message.DeliveryReport
import com.tbot.ruler.service.DeliveryReportListenerService
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class DeliveryReportListenerServiceSpec extends Specification {

    def "single waiter per message id"() {
        given:
        def service = new DeliveryReportListenerService();
        def msg1 = 123l;
        def msg2 = 234l;

        when:
        service.settleFuture(msg1);
        service.settleFuture(msg2);

        then:
        service.taskMap.get(msg1).getClass() == CompletableFuture.class;
        service.taskMap.get(msg2).getClass() == CompletableFuture.class;
    }

    def "waiters for mixed message ids"() {
        given:
        def service = new DeliveryReportListenerService();
        def msg1 = 123l;
        def msg2 = 234l;
        def msg3 = 345l;

        when:
        service.settleFuture(msg1);
        service.settleFuture(msg2);
        service.settleFuture(msg2);
        service.settleFuture(msg3);
        service.settleFuture(msg3);
        service.settleFuture(msg3);

        then:
        CompletableFuture.isAssignableFrom(service.taskMap.get(msg1).getClass());
        Set.isAssignableFrom(service.taskMap.get(msg2).getClass());
        ((Set) service.taskMap.get(msg2)).size() == 2;
        Set.isAssignableFrom(service.taskMap.get(msg3).getClass());
        ((Set) service.taskMap.get(msg3)).size() == 3;
    }

    def "map key empty after removing all waiters"() {
        given:
        def service = new DeliveryReportListenerService();
        def msg1 = 123l;
        def msg2 = 234l;

        when:
        def future1 = service.settleFuture(msg1);
        def future2 = service.settleFuture(msg2);
        def future3 = service.settleFuture(msg2);

        service.removeFuture(msg1, future1);
        service.removeFuture(msg2, future2);
        service.removeFuture(msg2, future3);

        then:
        service.taskMap.get(msg1) == null;
        service.taskMap.get(msg2) == null;
    }

    def "map key empty after delivering message"() {
        given:
        def service = new DeliveryReportListenerService();
        def msg1 = 123l;
        def msg2 = 234l;

        when:
        service.settleFuture(msg1);
        service.settleFuture(msg2);
        service.settleFuture(msg2);

        service.acceptDeliveryReport(DeliveryReport.builder().relatedMessageId(msg1).build());
        service.acceptDeliveryReport(DeliveryReport.builder().relatedMessageId(msg2).build());

        then:
        service.taskMap.get(msg1) == null;
        service.taskMap.get(msg2) == null;
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
        def messageId = 123;
        def service = new DeliveryReportListenerService();
        def report = DeliveryReport.builder().relatedMessageId(messageId).build();

        when:
        def waiter = hangOnWaiter(messageId, service, 60_000);
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
