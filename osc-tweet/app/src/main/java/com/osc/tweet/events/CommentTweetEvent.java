package com.osc.tweet.events;

import com.osc4j.ds.tweet.TweetListItem;

/**
 * Comment on a tweet object {@link com.osc4j.ds.tweet.TweetListItem}.
 *
 * @author Xinyue Zhao
 */
public final class CommentTweetEvent {
	/**
	 * A tweet object.
	 */
	private TweetListItem mTweetListItem;

	/**
	 * Constructor of {@link CommentTweetEvent}
	 * @param tweetListItem A tweet object {@link com.osc4j.ds.tweet.TweetListItem}.
	 */
	public CommentTweetEvent(TweetListItem tweetListItem) {
		mTweetListItem = tweetListItem;
	}

	/**
	 *
	 * @return A tweet object {@link com.osc4j.ds.tweet.TweetListItem}.
	 */
	public TweetListItem getTweetListItem() {
		return mTweetListItem;
	}
}
