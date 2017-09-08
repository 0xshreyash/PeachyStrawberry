package com.comp30022.helium.strawberry.pattern;

public interface Publisher<T> {
    void registerSubscriber(Subscriber<T> sub);
    void deregisterSubscriber(Subscriber<T> sub);
}
