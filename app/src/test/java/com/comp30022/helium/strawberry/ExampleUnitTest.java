package com.comp30022.helium.strawberry;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        System.out.println("[Test] Running example unit test");
        assertEquals(4, 2 + 2);
    }

    @Test
    public void fake_failTest() throws Exception {
        System.out.println("[Test] Running fail test");
        throw new Exception("this test will fail immediately");
    }
}