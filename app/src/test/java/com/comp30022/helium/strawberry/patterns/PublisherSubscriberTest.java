package com.comp30022.helium.strawberry.patterns;

import com.comp30022.helium.strawberry.mocks.MockPublisher;
import com.comp30022.helium.strawberry.mocks.MockSubscriber;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by noxm on 26/08/17.
 */
public class PublisherSubscriberTest {
    @Mock
    MockPublisher mockPublisher;

    @Mock
    List<MockSubscriber> mockSubscriberList;

    /**
<<<<<<< HEAD
     * Setup befoere the tests is run
=======
     * Setup befoere the tests are run
>>>>>>> develop
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        mockPublisher = new MockPublisher();
        mockSubscriberList = new ArrayList<>();
        Integer value = 0;
        for(int i = 0; i <= 10; i ++){
            mockSubscriberList.add(new MockSubscriber(value ++));
        }
    }


    /**
     * Test whether the publisher and subscriber works correctly or not.
     */
    @Test
    public void publish_oneSubscriber() throws Exception {
        for(int i = 0; i <= 10; i ++){
            mockPublisher.registerSubscriber(mockSubscriberList.get(i));
        }
        assertEquals(11, mockPublisher.getMockList().size());

        mockPublisher.deregisterSubscriber(mockSubscriberList.get(10));
        assertEquals(10, mockPublisher.getMockList().size());

        Integer newlocation = 50;
        Integer val;
        mockPublisher.notifyAllSubscribers(newlocation);
        for(int i = 0; i < 10; i ++){
            val = mockPublisher.getMockList().get(i).getLocation();
            assertEquals(newlocation, val);
        }
    }
}