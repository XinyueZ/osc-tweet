package com.osc.tweet.events;

import android.support.annotation.Nullable;

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
	 * Some text to comment on the tweet item.
	 */
	private @Nullable String mComment;

	/**
	 * Constructor of {@link CommentTweetEvent}.
	 * @param tweetListItem A tweet object {@link com.osc4j.ds.tweet.TweetListItem}.
	 */
	public CommentTweetEvent(TweetListItem tweetListItem) {
		mTweetListItem = tweetListItem;
	}

	/**
	 *  Constructor of {@link CommentTweetEvent}. To comment directly.
	 * @param tweetListItem  A tweet object {@link com.osc4j.ds.tweet.TweetListItem}.
	 * @param comment To comment directly.
	 */
	public CommentTweetEvent(TweetListItem tweetListItem, @Nullable String comment) {
		mTweetListItem = tweetListItem;
		mComment = comment;
	}

	/**
	 *
	 * @return A tweet object {@link com.osc4j.ds.tweet.TweetListItem}.
	 */
	public TweetListItem getTweetListItem() {
		return mTweetListItem;
	}

	/**
	 *
	 * @return  Some text to comment on the tweet item.
	 */
	public @Nullable String getComment() {
		return mComment;
	}
}
