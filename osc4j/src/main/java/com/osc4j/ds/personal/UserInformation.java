package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.osc4j.ds.tweet.TweetListItem;

public final class UserInformation implements Serializable{
	@SerializedName("status")
	private int mStatus;
	@SerializedName("user")
	private User mUser;
	@SerializedName("tweets")
	private List<TweetListItem> mTweets;

	public UserInformation(int status, User user, List<TweetListItem> tweets) {
		mStatus = status;
		mUser = user;
		mTweets = tweets;
	}

	public int getStatus() {
		return mStatus;
	}

	public User getUser() {
		return mUser;
	}


	public List<TweetListItem> getTweets() {
		return mTweets;
	}
}
