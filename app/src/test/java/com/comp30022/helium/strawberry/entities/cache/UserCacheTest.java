package com.comp30022.helium.strawberry.entities.cache;
import com.comp30022.helium.strawberry.mocks.MockUser;
import com.comp30022.helium.strawberry.mocks.MockUserCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

/**
 * Created by jjjjessie on 14/10/17.
 */
public class UserCacheTest {

    @Mock
    MockUserCache mockUserCache;

    @Mock
    MockUser mockUser;

    @Before
    public void setUp() throws Exception {
        mockUserCache = MockUserCache.getInstance();
        mockUser = MockUser.getMockUser("1234");
    }

    @Test
    public void put() throws Exception {
        mockUserCache.put("key", mockUser);
        MockUser testUser = mockUserCache.get("key");
    }

    @Test
    public void get() throws Exception {
        mockUserCache.get("key");
        MockUser testUser = mockUserCache.get("key");
    }

    @Test
    public void getInstance() throws Exception {

    }

}