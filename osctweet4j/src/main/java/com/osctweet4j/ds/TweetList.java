package com.osctweet4j.ds;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class TweetList implements Serializable  {
	@SerializedName("notice")
	private Notice mNotice;
	@SerializedName("tweetlist")
	private List<TweetListItem> mTweetListItems;

	public TweetList(Notice notice, List<TweetListItem> tweetListItems) {
		mNotice = notice;
		mTweetListItems = tweetListItems;
	}

	public Notice getNotice() {
		return mNotice;
	}

	public void setNotice(Notice notice) {
		mNotice = notice;
	}

	public List<TweetListItem> getTweetListItems() {
		return mTweetListItems;
	}

	public void setTweetListItems(List<TweetListItem> tweetListItems) {
		mTweetListItems = tweetListItems;
	}
}
