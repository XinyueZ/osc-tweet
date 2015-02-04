package com.osc.tweet.app.fragments;

import java.io.IOException;

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
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.TweetListAdapter;
import com.osc.tweet.utils.Prefs;
import com.osctweet4j.OscApi;
import com.osctweet4j.OscTweetException;
import com.osctweet4j.OscTweetUnauthorizationException;
import com.osctweet4j.WebDialog;
import com.osctweet4j.bus.AuthDoneEvent;
import com.osctweet4j.ds.TweetList;

/**
 * A list shows all tweets.
 */
public final class TweetListFragment extends BaseFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_tweet_list;

	private TweetListAdapter mAdp;
	private RecyclerView mRv;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.osctweet4j.bus.AuthDoneEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osctweet4j.bus.AuthDoneEvent}.
	 */
	public void onEvent(AuthDoneEvent e) {
getTweetList();
	}

	//------------------------------------------------
	/**
	 * Create an instance of {@link com.osc.tweet.app.fragments.TweetListFragment}.
	 * @param context {@link android.content.Context}.
	 * @return An instance of {@link com.osc.tweet.app.fragments.TweetListFragment}.
	 */
	public static Fragment newInstance(Context context) {
		return TweetListFragment.instantiate(context, TweetListFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRv = (RecyclerView) view.findViewById(R.id.tweet_list_rv);
		mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new TweetListAdapter(null));
	}

	@Override
	public void onResume() {
		super.onResume();
		getTweetList();
	}

	private void getTweetList( ) {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, TweetList, TweetList>() {
			@Override
			protected TweetList doInBackground(Object... params) {
				try {
					TweetList tweetList = OscApi.tweetList(App.Instance, 1);
					return tweetList;
				} catch (IOException e) {
					return null;
				} catch (OscTweetException e) {
					return  null;
				} catch (OscTweetUnauthorizationException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(TweetList tweetList) {
				super.onPostExecute(tweetList);
				if(tweetList == null) {
					WebDialog.newInstance(getActivity()).show(getChildFragmentManager(), null);
				} else {
					mAdp.setData(tweetList.getTweetListItems());
					mAdp.notifyDataSetChanged();
				}
			}
		});
	}


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
