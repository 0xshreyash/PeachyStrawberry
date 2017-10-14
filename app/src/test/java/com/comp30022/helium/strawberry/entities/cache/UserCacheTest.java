package com.comp30022.helium.strawberry.entities.cache;
import com.comp30022.helium.strawberry.mocks.MockUser;
import com.comp30022.helium.strawberry.mocks.MockUserCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by jjjjessie on 14/10/17.
 */
public class UserCacheTest {
    @Mock
    MockUserCache mockUserCache;

    @Mock
    MockUser mockUser1;

    @Mock
    MockUser mockUser2;

    /**
     * Setup befoere the tests are run
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        mockUserCache = MockUserCache.getInstance();
        mockUser1 = MockUser.getMockUser("1234");
        mockUser2 = MockUser.getMockUser("5678");
    }

    /**
     * Make sure the mock user has been actually put into the cache.
     * And test whether the put mock user is the correct one.
     */
    @Test
    public void put() throws Exception {
        mockUserCache.put("key1", mockUser1);
        Field cacheField = MockUserCache.getInstance().getClass().getDeclaredField("cache");
        cacheField.setAccessible(true);
        HashMap<String, MockUser> cacheMap =
                (HashMap<String, MockUser>) cacheField.get(MockUserCache.getInstance());

        MockUser testUser = cacheMap.get("key1");
        assertEquals(mockUser1, testUser);
    }

    /**
     * Test whether the cache can return the correctly required user.
     */
    @Test
    public void get() throws Exception {
        mockUserCache.put("key2", mockUser2);
        mockUserCache.get("key2");
        MockUser testUser = mockUserCache.get("key2");
        assertEquals(mockUser2, testUser);
    }

}