package com.comp30022.helium.strawberry.patterns;

public interface Publisher<T> {
    void registerSubscriber(Subscriber<T> sub);
    void deregisterSubscriber(Subscriber<T> sub);
}
