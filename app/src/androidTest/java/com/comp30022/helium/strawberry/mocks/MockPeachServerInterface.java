package com.comp30022.helium.strawberry.mocks;

import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.patterns.Event;
import com.comp30022.helium.strawberry.patterns.Subscriber;

/**
 * Created by noxm on 10/10/17.
 */

public class MockPeachServerInterface extends PeachServerInterface{
    // Shreyash's information
    public final static String SAMPLE_FB_TOKEN = "EAAJ5bdPqZCIIBAPKtZBS1ZCsH2Q4mB1adeG8cFBFrZB29TMvZCmxCOEwygknWpS0N7Xesw8Qy9YdLBpZC34iESnRZBTKDBTH6yZAJj5veuLE3p6LUUkZCVKtZCPsH7l54GQPjcZCCwsZAWGMdI512XvBwRyR3CCoZC03AqwdA0STGDtiJoAsZBZArTClA4OsEhTopUOMcwaZC7DBK0gkqprZBK5Lk4ZB8G";
    public final static String SAMPLE_ID_SELF = "59cf7b3c5a52f46d982b340a";

    // Max's id
    public final static String SAMPLE_ID = "59cf7b1e2f63f07468f2c77a";

    public MockPeachServerInterface(Subscriber<Event> toNotify) {
        super(SAMPLE_FB_TOKEN, toNotify);
    }
}
