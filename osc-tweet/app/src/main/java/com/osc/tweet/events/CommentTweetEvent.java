package com.osc.tweet.events;

import android.support.annotation.Nullable;

import com.osc4j.ds.comment.Comment;
import com.osc4j.ds.tweet.TweetListItem;

/**
 * Comment on   {@link com.osc4j.ds.tweet.TweetListItem} or write comment to a {@link com.osc4j.ds.comment.Comment}(reply).
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
	private @Nullable
	Comment mComment;


	/**
	 *  Constructor of {@link CommentTweetEvent}. To comment directly.
	 * @param tweetListItem  A tweet object {@link com.osc4j.ds.tweet.TweetListItem}.
	 * @param comment The {@code comment} will be replied. Comment the {@code comment} or reply the {@code comment}.
	 */
	public CommentTweetEvent(TweetListItem tweetListItem, @Nullable Comment comment) {
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
	 * @return  The {@code comment} will be replied. Comment the {@code comment} or reply the {@code comment}.
	 */
	public @Nullable Comment getComment() {
		return mComment;
	}
}
