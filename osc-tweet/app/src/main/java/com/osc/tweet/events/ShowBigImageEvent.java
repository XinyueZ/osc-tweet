package com.osc.tweet.events;


import com.osctweet4j.ds.TweetListItem;

public final class ShowBigImageEvent {
	private TweetListItem mTweetListItem;

	public ShowBigImageEvent(TweetListItem tweetListItem) {
		mTweetListItem = tweetListItem;
	}

	public TweetListItem getTweetListItem() {
		return mTweetListItem;
	}
}
