package com.osc4j.ds;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class TweetList implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("tweets")
	private List<TweetListItem> mTweets;

	public TweetList(int status, List<TweetListItem> tweets) {
		mStatus = status;
		mTweets = tweets;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int status) {
		mStatus = status;
	}

	public List<TweetListItem> getTweets() {
		return mTweets;
	}

	public void setTweets(List<TweetListItem> tweets) {
		mTweets = tweets;
	}
}
