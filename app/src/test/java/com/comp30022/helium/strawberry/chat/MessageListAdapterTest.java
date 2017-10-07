package com.comp30022.helium.strawberry.chat;

/**
 * Created by shreyashpatodia on 03/10/17.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.test.mock.MockContext;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comp30022.helium.strawberry.components.chat.Message;
import com.comp30022.helium.strawberry.components.chat.MessageListAdapter;
import com.comp30022.helium.strawberry.components.friends.FriendListAdapter;
import com.comp30022.helium.strawberry.entities.User;

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
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test for MessageListAdapter created to be able to have a list of friends.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, LayoutInflater.class, MessageListAdapter.class})
public class MessageListAdapterTest {

    @Mock
    LayoutInflater mockInflater;

    @Mock
    View mockView;

    @Mock
    ViewGroup mockParent;

    private Context mockContext;
    private MessageListAdapter mockAdapter;

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
        List<Message> messages = new ArrayList<>();
        User one = new User("123", "Shreyash");
        User two= new User("345", "Michael");
        messages.add(new Message("Hey how are you", one, 12345677));
        messages.add(new Message("I am good as good can be ", one, 12345699));
        //mockStatic(FacebookFragment.class);
        mockContext = new MockContext();
        mockAdapter = new MessageListAdapter(mockContext, messages);
    }

    /**
     * Test if the item inputs into the message list adapter are
     * correct.
     */
    @Test
    public void testItemCount() {
        assertEquals(2, mockAdapter.getItemCount());
    }

    @Test
    public void testItemCountTwo() {
        MessageListAdapter emptyAdapter = new MessageListAdapter(mockContext,
                new ArrayList<Message>());
        assertEquals(0, emptyAdapter.getItemCount());
    }


    @Test
    public void testProvidedViewHolder() {

        when(mockParent.getContext()).thenReturn(mockContext);
        when(LayoutInflater.from(mockContext)).thenReturn(mockInflater);
        when(mockInflater.inflate(anyInt(), eq(mockParent), eq(false))).thenReturn(mockView);

        RecyclerView.ViewHolder messageHolder = mockAdapter.onCreateViewHolder(mockParent,
                mockAdapter.VIEW_TYPE_MESSAGE_SENT);
        assertTrue(messageHolder instanceof MessageListAdapter.SentMessageHolder);
        MessageListAdapter.SentMessageHolder sentMessageHolder =
                (MessageListAdapter.SentMessageHolder)messageHolder;

        assertEquals(mockView, sentMessageHolder.getItemView());

        RecyclerView.ViewHolder messageHolderTwo = mockAdapter.onCreateViewHolder(mockParent,
                mockAdapter.VIEW_TYPE_MESSAGE_RECEIVED);
        assertTrue(messageHolderTwo instanceof MessageListAdapter.ReceivedMessageHolder);
        MessageListAdapter.ReceivedMessageHolder receivedMessageHolder =
                (MessageListAdapter.ReceivedMessageHolder)messageHolderTwo;

        assertEquals(mockView, receivedMessageHolder.getItemView());

        RecyclerView.ViewHolder messageHolderThree = mockAdapter.onCreateViewHolder(mockParent,
                -1);
        assertNull(messageHolderThree);
    }
}
