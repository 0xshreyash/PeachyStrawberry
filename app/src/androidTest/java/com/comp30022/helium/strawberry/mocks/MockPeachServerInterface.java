package com.comp30022.helium.strawberry.mocks;

import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.patterns.Event;
import com.comp30022.helium.strawberry.patterns.Subscriber;

/**
 * Created by noxm on 10/10/17.
 */

public class MockPeachServerInterface extends PeachServerInterface{
    // Shreyash's information
    public final static String SAMPLE_FB_TOKEN = "EAAJ5bdPqZCIIBAPO77SesRM0t8Ec7llEFdbjTZCwJe1ZAPo4FZAkrdHeZCFiKxE8xMhf0b8ZB4wAdZCyrNib7jZBUzkwChqqnEVoabcN5jaf7xOg7oSENSC7MAwI8piQV40i0UAPbuEdqQjNob89V53WXJhLf2DsBy8ZD";
    public final static String SAMPLE_ID_SELF = "59cf7b3c5a52f46d982b340a";

    // Max's id
    public final static String SAMPLE_ID = "59cf7b1e2f63f07468f2c77a";

    public MockPeachServerInterface(Subscriber<Event> toNotify) {
        super(SAMPLE_FB_TOKEN, toNotify);
    }
}
