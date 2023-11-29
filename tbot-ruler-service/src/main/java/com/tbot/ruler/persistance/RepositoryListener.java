package com.tbot.ruler.persistance;

public interface RepositoryListener<E> {

    default void entityInserted(E insertedEntity) {};

    default void entityUpdated(E updatedEntity) {};

    default void entityDeleted(E deletedEntity) {};
}
