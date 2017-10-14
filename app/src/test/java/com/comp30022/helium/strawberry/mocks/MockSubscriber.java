package com.comp30022.helium.strawberry.mocks;

import android.location.Location;

import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Subscriber;

/**
 * Created by jjjjessie on 7/10/17.
 */

public class MockSubscriber implements Subscriber<Integer> {
    private Integer location = null;

    public MockSubscriber(Integer location){
        this.location = location;
    }

    public Integer getLocation(){
        return location;
    }

    @Override
    public void update(Integer info) {
        location = info;
    }
}
