package com.osc.tweet.app.adapters;

import java.util.List;

import com.osc.tweet.R;
import com.osc4j.ds.tweet.TweetListItem;

/**
 * The adapter for tweet-list on user-info.
 *
 * @author Xinyue Zhao
 */
public final class UserInfoTweetListAdapter extends BaseTweetListAdapter {
	public UserInfoTweetListAdapter(List<TweetListItem> data) {
		super(data);
	}

	@Override
	protected int getItemLayout() {
		return  R.layout.item_user_info_tweet_line;
	}
}
