package com.comp30022.helium.strawberry.entities;

import com.comp30022.helium.strawberry.mocks.MockUser;
import com.comp30022.helium.strawberry.mocks.MockUserCache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Can't test User.java due to LRU cache that wasn't mocked.
 * Test similar mocked user class instead - MockUser.java
 */
public class UserTest {
    private User mockUser;
    private static final String USERID = "ID";
    private static final String USERNAME = "NAME";
    private static final String FBID = "FBID";

    @Before
    public void setup() {
    }

    /**
     * We don't want to explicitly make clearCache() a public method in MockUserCache
     * when it's not needed. Therefore, we use reflection to clear the cache.
     */
    @After
    public void tearDown() {
        try {
            Field f = MockUserCache.getInstance().getClass().getDeclaredField("cache");
            f.setAccessible(true);
            HashMap<?, ?> cacheField =
                    (HashMap<String, MockUser>) f.get(MockUserCache.getInstance());
            cacheField.clear();
//            System.out.println("Cleared cache!");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void userConstructor_IDonly() {
        mockUser = MockUser.getMockUser(USERID);
        assertNotNull(mockUser);
        assertEquals(USERID, mockUser.getId());
    }

    @Test
    public void userConstructor_ID_userName() {
        mockUser = MockUser.getMockUser(USERID, USERNAME);
        assertNotNull(mockUser);
        assertEquals(USERID, mockUser.getId());
        assertEquals(USERNAME, mockUser.getUsername());
    }

    @Test
    public void userConstructor_ID_username_FB() {
        mockUser = MockUser.getMockUser(USERID, USERNAME, FBID);
        assertNotNull(mockUser);
        assertEquals(USERID, mockUser.getId());
        assertEquals(USERNAME, mockUser.getUsername());
        assertEquals(FBID, mockUser.getFacebookId());
    }

    /**
     * make sure that the user that comes back from the cached map
     * has the EXACT same reference. i.e. the new user (same user)
     * is cached and returned, rather than constructed again!
     */
    @Test
    public void userConstructor_IDonly_cache() {
        mockUser = MockUser.getMockUser(USERID);
        assertNotNull(mockUser);
        assertEquals(USERID, mockUser.getId());

        User mockUser2 = MockUser.getMockUser(USERID);
        // if check that the .getMockUser method IS accessing the cache.
        // if it's not, it will construct a new user and the reference pointer value
        // will be different. Here, we're expecting it to be the same because we cached
        // the original user!
        assertEquals(mockUser2, mockUser);
    }

    /**
     * make sure that the user that comes back from the cached map
     * has the EXACT same reference. i.e. the new user (same user)
     * is cached and returned, rather than constructed again!
     */
    @Test
    public void userConstructor_ID_userName_cache() {
        mockUser = MockUser.getMockUser(USERID, USERNAME);
        assertNotNull(mockUser);
        assertEquals(USERID, mockUser.getId());
        assertEquals(USERNAME, mockUser.getUsername());

        User mockUser2 = MockUser.getMockUser(USERID, USERNAME);
        // if check that the .getMockUser method IS accessing the cache.
        // if it's not, it will construct a new user and the reference pointer value
        // will be different. Here, we're expecting it to be the same because we cached
        // the original user!
        assertEquals(mockUser2, mockUser);
    }

    /**
     * make sure that the user that comes back from the cached map
     * has the EXACT same reference. i.e. the new user (same user)
     * is cached and returned, rather than constructed again!
     */
    @Test
    public void userConstructor_ID_username_FB_cache() {
        mockUser = MockUser.getMockUser(USERID, USERNAME, FBID);
        assertNotNull(mockUser);
        assertEquals(USERID, mockUser.getId());
        assertEquals(USERNAME, mockUser.getUsername());
        assertEquals(FBID, mockUser.getFacebookId());

        User mockUser2 = MockUser.getMockUser(USERID, USERNAME, FBID);
        // if check that the .getMockUser method IS accessing the cache.
        // if it's not, it will construct a new user and the reference pointer value
        // will be different. Here, we're expecting it to be the same because we cached
        // the original user!
        assertEquals(mockUser2, mockUser);
    }

    /**
     * make sure that the user that comes back from the cached map
     * has the exact same id
     */
    @Test
    public void userConstructor_IDonly_cache_equals() {
        mockUser = MockUser.getMockUser(USERID);
        assertNotNull(mockUser);
        assertEquals(USERID, mockUser.getId());

        User mockUser2 = MockUser.getMockUser(USERID);

        assertEquals(mockUser.getId(), mockUser2.getId());
    }

    /**
     * make sure that the user that comes back from the cached map
     * has the exact same id, and username
     */
    @Test
    public void userConstructor_ID_userName_cache_equals() {
        mockUser = MockUser.getMockUser(USERID, USERNAME);
        assertNotNull(mockUser);
        assertEquals(USERID, mockUser.getId());
        assertEquals(USERNAME, mockUser.getUsername());

        User mockUser2 = MockUser.getMockUser(USERID, USERNAME);

        assertEquals(mockUser.getId(), mockUser2.getId());
        assertEquals(mockUser.getUsername(), mockUser2.getUsername());
    }

    /**
     * make sure that the user that comes back from the cached map
     * has the exact same id, and username, and facebook id
     */
    @Test
    public void userConstructor_ID_username_FB_cache_equals() {
        mockUser = MockUser.getMockUser(USERID, USERNAME, FBID);
        assertNotNull(mockUser);
        assertEquals(USERID, mockUser.getId());
        assertEquals(USERNAME, mockUser.getUsername());
        assertEquals(FBID, mockUser.getFacebookId());

        User mockUser2 = MockUser.getMockUser(USERID, USERNAME, FBID);

        assertEquals(mockUser.getId(), mockUser2.getId());
        assertEquals(mockUser.getUsername(), mockUser2.getUsername());
        assertEquals(mockUser.getFacebookId(), mockUser2.getFacebookId());
    }

    /**
     * Given that we "find" 2 differnet users, the SHOULD BE DIFFERENT!
     */
    @Test
    public void testCacheDifferentUsers() {
        mockUser = MockUser.getMockUser(USERID);
        User mockUser2 = MockUser.getMockUser(USERID+"1");
        User mockUser3 = MockUser.getMockUser(USERID);

        assertNotEquals(mockUser.getId(), mockUser2.getId());

        // make sure same id
        assertEquals(mockUser.getId(), mockUser3.getId());
        // make sure same reference pointer
        assertEquals(mockUser, mockUser3);

        // make sure it's cached properly when users are different
        assertNotEquals(mockUser2, mockUser);
        assertNotEquals(mockUser2, mockUser3);
    }
}