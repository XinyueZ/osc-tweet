package com.osc.tweet.app.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.osc.tweet.R;
import com.osc.tweet.app.adapters.FriendsListAdapter;
import com.osc.tweet.events.CloseFriendsListEvent;
import com.osc.tweet.utils.Prefs;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc4j.OscApi;
import com.osc4j.OscTweetException;
import com.osc4j.ds.personal.Friend;
import com.osc4j.ds.personal.Friends;
import com.osc4j.ds.personal.FriendsList;

import de.greenrobot.event.EventBus;

/**
 * A list of all friends, inc. Fans, Focus etc.
 */
public final class FriendsListFragment extends BaseFragment {

	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_friends_list;
	/**
	 * Adapter for {@link #mRv} to show all tweets.
	 */
	private FriendsListAdapter mAdp;
	/**
	 * List container for showing all friends.
	 */
	private RecyclerView mRv;
	/**
	 * Loading indicator.
	 */
	private View mPbV;

	/**
	 * Create an instance of {@link com.osc.tweet.app.fragments.FriendsListFragment}.
	 * @param context {@link android.content.Context}.
	 * @return {@link com.osc.tweet.app.fragments.FriendsListFragment}.
	 */
	public static Fragment newInstance(Context context) {
		return FriendsListFragment.instantiate(context, FriendsListFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(true);
		mRv = (RecyclerView) view.findViewById(R.id.friends_list_rv);
		mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new FriendsListAdapter(null));
		view.findViewById(R.id.close_friends_list_btn).setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
//				getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
//						R.anim.slide_in_from_right, R.anim.slide_out_to_right, R.anim.slide_in_from_right,
//						R.anim.slide_out_to_right).commit();
//						.remove(FriendsListFragment.this).commit();

				getActivity().getSupportFragmentManager().popBackStack();
				EventBus.getDefault().post(new CloseFriendsListEvent());
			}
		});
		mPbV = view.findViewById(R.id.pb);
		getFriendsList();
	}


	/**
	 * Get tweets data and showing on list.
	 */
	private void getFriendsList() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, FriendsList, FriendsList>() {
			@Override
			protected FriendsList doInBackground(Object... params) {
				try {
					return OscApi.friendsList(getActivity());
				} catch (IOException e) {
					return null;
				} catch (OscTweetException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(FriendsList friendsList) {
				super.onPostExecute(friendsList);
				if (friendsList != null) {
					Friends friends = friendsList.getFriends();
					List<Friend> all = new ArrayList<>();
					List<Friend> fansList = friends.getFansList();
					if (fansList != null) {
						all.addAll(fansList);
					}

					List<Friend> focusList = friends.getFocusList();
					if (focusList != null) {
						all.addAll(focusList);
					}
					mAdp.setData(all);
					mAdp.notifyDataSetChanged();
					mPbV.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

}
