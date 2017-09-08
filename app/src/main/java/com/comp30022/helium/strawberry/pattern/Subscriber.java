package com.comp30022.helium.strawberry.pattern;

public interface Subscriber<T> {
    /**
     * updates the subscriber based on a generic information
     * @param info generic information passed on to all subscribers on change
     */
    void update(T info);
}
