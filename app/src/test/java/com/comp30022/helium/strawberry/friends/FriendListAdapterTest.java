package com.comp30022.helium.strawberry.friends;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.mock.MockContext;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.activities.FriendListTestActivity;
import com.comp30022.helium.strawberry.activities.fragments.FriendListFragment;
import com.comp30022.helium.strawberry.components.friends.FriendListAdapter;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Subscriber;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test for FriendListAdapterClass created to be able to have a list of friends.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, LayoutInflater.class, FriendListAdapter.class})
public class FriendListAdapterTest {

    @Mock
    LayoutInflater mockInflater;

    @Mock
    View mockView;

    @Mock
    ViewGroup mockParent;

    @Mock
    StrawberryApplication mockStrawberryApplication;

    @Mock
    SharedPreferences mockSharedPreferences;

    @Mock
    FriendListFragment mockFriendListFragment;

    @Mock
    FriendListTestActivity mockActivity;

    private Context mockContext;

    /**
     * Setup befoere the tests is run
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        mockStatic(LayoutInflater.class);
        mockStatic(Log.class);
        mockStatic(FriendListAdapter.class);

        //mockStatic(FacebookFragment.class);
        mockContext = new MockContext() {
            public SharedPreferences getSharedPreferences(String s, int i) {
                Log.d("tests", "Im here");
                return mockSharedPreferences;
            }
        };

        mockStrawberryApplication = new StrawberryApplication() {
            public Context getApplicationContext() {
                return mockContext;
            }
        };

        mockActivity = new FriendListTestActivity();
        mockFriendListFragment = new FriendListFragment();

    }

    /**
     * Check if the getItemCount method works
     */
    @Test
    public void checkSize() {
        List<User> friendList = new ArrayList<>();
        FriendListFragment fragment = new FriendListFragment();
        friendList.add(User.getUser("1234", "Shreyash"));
        friendList.add(User.getUser("5678", "Harry"));
        Context fakeContext = new MockContext();
        FriendListAdapter adapter = new FriendListAdapter(fakeContext, friendList, fragment);
        assertEquals(2, adapter.getItemCount());
    }

    /**
     * Check if the getItemCount method works
     */
    @Test
    public void checkSizeTwo() {
        List<User> friendList = new ArrayList<>();
        FriendListFragment fragment = new FriendListFragment();
        Context fakeContext = new MockContext();
        FriendListAdapter adapter = new FriendListAdapter(fakeContext, friendList, fragment);
        assertEquals(0, adapter.getItemCount());
    }

    /**
     * Check if selected items get set correctly and the types are
     * currently defined.
     */
    @Test
    public void selectedItemsCheck() {

        List<User> friendList = new ArrayList<>();
        FriendListFragment fragment = new FriendListFragment();
        friendList.add(User.getUser("1234", "Shreyash"));
        friendList.add(User.getUser("5678", "Harry"));
        Context fakeContext = new MockContext();
        FriendListAdapter adapter = new FriendListAdapter(fakeContext, friendList, fragment);
        adapter.setSelectedPosition(0);
        int type = adapter.getItemViewType(0);
        assertEquals(FriendListAdapter.SELECTED_FRIEND, type);
        type = adapter.getItemViewType(1);
        assertEquals(FriendListAdapter.FRIEND, type);
        adapter.setSelectedPosition(1);
        type = adapter.getItemViewType(1);
        assertEquals(FriendListAdapter.SELECTED_FRIEND, type);
        type = adapter.getItemViewType(0);
        assertEquals(FriendListAdapter.FRIEND, type);
    }

    /**
     * Tests if the publishes subscriber behaviour of the FriendHolder works or
     * not
     */
    @Test
    public void checkViewHolderCreation() {
        mockStatic(Log.class);

        List<User> friendList = new ArrayList<>();
        FriendListFragment fragment = new FriendListFragment();
        friendList.add(User.getUser("1234", "Shreyash"));
        friendList.add(User.getUser("5678", "Harry"));
        Context fakeContext = new MockContext();
        FriendListAdapter adapter = new FriendListAdapter(fakeContext, friendList, fragment);

        when(mockParent.getContext()).thenReturn(mockContext);

        when(LayoutInflater.from(mockContext)).thenReturn(mockInflater);


        when(mockInflater.inflate(anyInt(), eq(mockParent), eq(false))).thenReturn(mockView);

        FriendListAdapter.FriendHolder viewHolder = adapter.onCreateViewHolder(mockParent, FriendListAdapter.FRIEND);

        assertNotNull(viewHolder);

        assertEquals(viewHolder.getItemView(), mockView);

    }

    /**
     * Check for failure of creation when the view type entered is
     * invalid.
     */
    @Test
    public void checkViewHolderCreationTwo() {
        List<User> friendList = new ArrayList<>();
        FriendListFragment fragment = new FriendListFragment();
        friendList.add(User.getUser("1234", "Shreyash"));
        friendList.add(User.getUser("5678", "Harry"));
        Context fakeContext = new MockContext();
        FriendListAdapter adapter = new FriendListAdapter(fakeContext, friendList, fragment);

        when(mockParent.getContext()).thenReturn(mockContext);

        when(LayoutInflater.from(mockContext)).thenReturn(mockInflater);

        when(mockInflater.inflate(anyInt(), eq(mockParent), eq(false))).thenReturn(mockView);

        FriendListAdapter.FriendHolder viewHolder = adapter.onCreateViewHolder(mockParent, 234);

        assertNull(viewHolder);

    }

    /**
     * Class created specifically in order to test if FriendHolder publishing works
     * correctly or not.
     */
    public class TestViewHolderPublishing implements Subscriber<Integer> {

        private int calls = 0;

        public void update(Integer info) {
            this.calls++;
        }

        public int getCalls() {
            return calls;
        }

        public void setCalls(int calls) {
            this.calls = calls;
        }
    }

    /**
     * Check if the viewHolder actually publishes things.
     */
    @Test
    public void checkFriendHolderPublishing() {
        List<User> friendList = new ArrayList<>();
        FriendListFragment fragment = new FriendListFragment();
        friendList.add(User.getUser("1234", "Shreyash"));
        friendList.add(User.getUser("5678", "Harry"));

        when(mockParent.getContext()).thenReturn(mockContext);
        when(LayoutInflater.from(mockContext)).thenReturn(mockInflater);
        when(mockInflater.inflate(anyInt(), eq(mockParent), eq(false))).thenReturn(mockView);
        FriendListAdapter friendListAdapter = new FriendListAdapter(mockContext,
                friendList, fragment);
        //when(mockFriendListFragment.getView()).thenReturn(mockView);
        FriendListAdapter.FriendHolder holder = friendListAdapter.new FriendHolder(mockParent);
        TestViewHolderPublishing viewHolderPublishing = new TestViewHolderPublishing();
        holder.registerSubscriber(viewHolderPublishing);
        holder.notifyAllSubscribers();
        assertEquals(1, viewHolderPublishing.getCalls());
        holder.deregisterSubscriber(viewHolderPublishing);
        holder.notifyAllSubscribers();
        assertEquals(1,viewHolderPublishing.getCalls());
    }
}
