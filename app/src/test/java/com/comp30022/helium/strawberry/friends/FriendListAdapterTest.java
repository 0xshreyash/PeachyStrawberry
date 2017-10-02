package com.comp30022.helium.strawberry.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.test.mock.MockContext;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comp30022.helium.strawberry.activities.fragments.FriendListFragment;
import com.comp30022.helium.strawberry.components.friends.FriendListAdapter;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Subscriber;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
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
@PrepareForTest({Log.class, LayoutInflater.class})
public class FriendListAdapterTest {

    @Mock
    LayoutInflater mockInflater;

    @Mock
    View mockView;

    @Mock
    ViewGroup mockParent;

    private Context mockContext;

    private FriendListAdapter adapter;

    /**
     * Setup befoere the tests is run
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        mockStatic(LayoutInflater.class);
        mockStatic(Log.class);
        mockContext = new MockContext();

    }


    /**
     * Check if the getItemCount method works
     */
    @Test
    public void checkSize() {
        List<User> friendList = new ArrayList<>();
        FriendListFragment fragment = new FriendListFragment();
        friendList.add(new User("1234", "Shreyash"));
        friendList.add(new User("5678", "Max"));
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
        friendList.add(new User("1234", "Shreyash"));
        friendList.add(new User("5678", "Max"));
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
     * Tests if the publishes subscriber behaviour of the FriendHolder works or
     * not
     */
    @Test
    public void checkViewHolderCreation() {
        mockStatic(Log.class);

        List<User> friendList = new ArrayList<>();
        FriendListFragment fragment = new FriendListFragment();
        friendList.add(new User("1234", "Shreyash"));
        friendList.add(new User("5678", "Max"));
        Context fakeContext = new MockContext();
        FriendListAdapter adapter = new FriendListAdapter(fakeContext, friendList, fragment);

        when(mockParent.getContext()).thenReturn(mockContext);

        // 6. mock the inflater that is returned by LayoutInflater.from()
        when(LayoutInflater.from(mockContext)).thenReturn(mockInflater);

        // 7. pass anyInt() as a resource id to care of R.layout.fragment_news_view_holder in onCreateViewHolder()
        when(mockInflater.inflate(anyInt(), eq(mockParent), eq(false))).thenReturn(mockView);

        FriendListAdapter.FriendHolder viewHolder = adapter.onCreateViewHolder(mockParent, FriendListAdapter.FRIEND);

        // OKAY straightfoward right?
        assertNotNull(viewHolder);

        assertEquals(viewHolder.getItemView(), mockView);

    }

    /**
     * Check for failure of creation when the view type entered is
     * invalid.
     */
    @Test
    public void checkViewHolderCreationTwo() {
        mockStatic(Log.class);

        List<User> friendList = new ArrayList<>();
        FriendListFragment fragment = new FriendListFragment();
        friendList.add(new User("1234", "Shreyash"));
        friendList.add(new User("5678", "Max"));
        Context fakeContext = new MockContext();
        FriendListAdapter adapter = new FriendListAdapter(fakeContext, friendList, fragment);

        when(mockParent.getContext()).thenReturn(mockContext);

        // 6. mock the inflater that is returned by LayoutInflater.from()
        when(LayoutInflater.from(mockContext)).thenReturn(mockInflater);

        // 7. pass anyInt() as a resource id to care of R.layout.fragment_news_view_holder in onCreateViewHolder()
        when(mockInflater.inflate(anyInt(), eq(mockParent), eq(false))).thenReturn(mockView);

        FriendListAdapter.FriendHolder viewHolder = adapter.onCreateViewHolder(mockParent, 234);

        // OKAY straightfoward right?
        assertNull(viewHolder);
        //assertEquals(viewHolder.getItemView(), mockView);

    }

    /**
     * Check if the viewHolder actually publishes things.
     */
    



}
