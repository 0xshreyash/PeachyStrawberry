package com.comp30022.helium.strawberry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Simple Unit Test Suite
 * @author Max Lee
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ApplicationPropertyTest.class,
        GoogleMapsUnitTest.class
})
public class InstrumentedTestSuite {
}
