package com.osc4j.ds.favorite;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.osc4j.ds.tweet.TweetListItem;

public final class TweetFavoritesList implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("favorites")
	private List<TweetListItem> mTweets;

	public TweetFavoritesList(int status, List<TweetListItem> tweets) {
		mStatus = status;
		mTweets = tweets;
	}

	public int getStatus() {
		return mStatus;
	}


	public List<TweetListItem> getTweets() {
		return mTweets;
	}

}
