package com.comp30022.helium.strawberry.components.map.exceptions;

/**
 * Created by noxm on 17/09/17.
 */

public class NoSuchMarkerException extends RuntimeException {
    public NoSuchMarkerException(String s) {
        super (s + " marker does not exist");
    }
}
