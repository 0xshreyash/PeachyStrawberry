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
    MockUser mockUser;

    /**
     * Setup befoere the tests are run
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        mockUserCache = MockUserCache.getInstance();
        mockUser = MockUser.getMockUser("1234");
    }

    /**
     * Make sure the mock user has been actually put into the cache.
     * And test whether the put mock user is the correct one.
     */
    @Test
    public void put() throws Exception {
        mockUserCache.put("key", mockUser);
        Field cacheField = MockUserCache.getInstance().getClass().getDeclaredField("cache");
        cacheField.setAccessible(true);
        HashMap<String, MockUser> cacheMap =
                (HashMap<String, MockUser>) cacheField.get(MockUserCache.getInstance());

        MockUser testUser = cacheMap.get("key");
        assertEquals(mockUser, testUser);
    }

    /**
     * Test whether the cache can return the correctly required user.
     */
    @Test
    public void get() throws Exception {
        mockUserCache.put("key", mockUser);
        mockUserCache.get("key");
        MockUser testUser = mockUserCache.get("key");
        assertEquals(mockUser, testUser);
    }

}