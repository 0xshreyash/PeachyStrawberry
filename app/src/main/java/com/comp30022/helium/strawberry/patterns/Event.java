package com.comp30022.helium.strawberry.patterns;

/**
 * Created by noxm on 23/09/17.
 */

public interface Event<S, K, V> {
    S getSource();
    K getKey();
    V getValue();
}
