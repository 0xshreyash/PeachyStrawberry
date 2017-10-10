package com.comp30022.helium.strawberry.mocks;

import com.comp30022.helium.strawberry.patterns.Publisher;
import com.comp30022.helium.strawberry.patterns.Subscriber;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jjjjessie on 7/10/17.
 */

public class MockPublisher implements Publisher<Integer> {
    private List<MockSubscriber> mockList;

    public MockPublisher() {
        mockList = new ArrayList<>();
    }

    @Override
    public void registerSubscriber(Subscriber<Integer> sub) {
        mockList.add((MockSubscriber)sub);
    }

    @Override
    public void deregisterSubscriber(Subscriber<Integer> sub) {
        mockList.remove((MockSubscriber)sub);
    }

    public void notifyAllSubscribers(Integer location) {
        // this method will call the update() method on all subscribers that are
        // registered.
        for (MockSubscriber sub : mockList) {
            sub.update(location);
        }
    }

    public List<MockSubscriber> getMockList(){
        return mockList;
    }
}
