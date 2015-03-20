package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.osc4j.ds.tweet.TweetListItem;

public final class UserInformation implements Serializable{
	@SerializedName("status")
	private int mStatus;
	@SerializedName("user")
	private People mPeople;
	@SerializedName("tweets")
	private List<TweetListItem> mTweets;

	public UserInformation(int status, People people, List<TweetListItem> tweets) {
		mStatus = status;
		mPeople = people;
		mTweets = tweets;
	}


	public int getStatus() {
		return mStatus;
	}

	public People getPeople() {
		return mPeople;
	}

	public List<TweetListItem> getTweets() {
		return mTweets;
	}
}
