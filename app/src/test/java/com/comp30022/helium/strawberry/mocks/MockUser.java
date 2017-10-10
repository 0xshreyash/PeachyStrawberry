package com.comp30022.helium.strawberry.mocks;

import android.util.Log;

import com.comp30022.helium.strawberry.entities.User;

/**
 * Created by noxm on 10/10/17.
 */

public class MockUser extends User {
    private static final String TAG = "MockUser";

    public static MockUser getMockUser(String id) {
        MockUserCache userCache = MockUserCache.getInstance();
        if (userCache.get(id) != null)
            userCache.get(id);

        MockUser user = new MockUser(id);
        userCache.put(id, user);
        return user;
    }

    public static MockUser getMockUser(String id, String username) {
        MockUserCache userCache = MockUserCache.getInstance();
        if (userCache.get(id) != null)
            userCache.get(id);

        MockUser user = new MockUser(id, username);
        userCache.put(id, user);
        return user;
    }

    public static MockUser getMockUser(String id, String username, String facebookId) {
        MockUserCache userCache = MockUserCache.getInstance();
        if (userCache.get(id) != null)
            userCache.get(id);

        MockUser user = new MockUser(id, username, facebookId);
        userCache.put(id, user);
        return user;
    }

    @Override
    protected void updateUserInfo() {
        Log.d(TAG, "Update user info is not supported in mock");
    }

    private MockUser(String id) {
        super(id);
    }

    private MockUser(String id, String username) {
        super(id, username);
    }

    private MockUser(String id, String username, String facebookId) {
        super(id, username, facebookId);
    }
}
