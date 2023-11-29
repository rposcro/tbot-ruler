package com.tbot.ruler.persistance;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractRepository<E> {

    private final List<RepositoryListener<E>> listeners = new LinkedList<>();

    public void addListener(RepositoryListener<E> listener) {
        listeners.add(listener);
    }

    protected void triggerInserted(E entity) {
        listeners.forEach(listener -> listener.entityInserted(entity));
    }

    protected void triggerUpdated(E entity) {
        listeners.forEach(listener -> listener.entityUpdated(entity));
    }

    protected void triggerDeleted(E entity) {
        listeners.forEach(listener -> listener.entityDeleted(entity));
    }
}
