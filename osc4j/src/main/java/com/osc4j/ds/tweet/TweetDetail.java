package com.osc4j.ds.tweet;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class TweetDetail implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("tweet")
	private  TweetListItem mTweet ;

	public TweetDetail(int status, TweetListItem tweet) {
		mStatus = status;
		mTweet = tweet;
	}


	public int getStatus() {
		return mStatus;
	}

	public TweetListItem getTweet() {
		return mTweet;
	}
}
