package com.tbot.ruler.persistance.json;

import java.util.concurrent.atomic.AtomicLong;

public class AbstractJsonFileRepository {

    private AtomicLong idSequence = new AtomicLong(1);

    protected long nextId() {
        return idSequence.getAndIncrement();
    }
}
