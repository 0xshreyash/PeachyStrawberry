package com.comp30022.helium.strawberry.entities;

/**
 * Created by noxm on 4/10/17.
 */

public abstract class StrawberryCallback<T> {
    public Object attribute;
    public abstract void run(T t);
}
