package com.osc.tweet.events;

import android.support.annotation.Nullable;

import com.osc4j.ds.comment.Comment;
import com.osc4j.ds.personal.Notice;
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
	 * A comment to tweet, the {@line #mTweetListItem}.
	 */
	private @Nullable
	Comment mComment;

	/**
	 * The notice of a comment to tweet, the {@line #mTweetListItem}.
	 */
	private @Nullable
	Notice mNotice;
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
	 * @param comment The {@code comment} will be replied. Reply the {@code comment}.
	 */
	public CommentTweetEvent(TweetListItem tweetListItem, @Nullable Comment comment) {
		mTweetListItem = tweetListItem;
		mComment = comment;
	}


	/**
	 *  Constructor of {@link CommentTweetEvent}. To comment directly.
	 * @param tweetListItem  A tweet object {@link com.osc4j.ds.tweet.TweetListItem}.
	 * @param notice The notice of a comment to tweet, the {@line #mTweetListItem}.
	 */
	public CommentTweetEvent(TweetListItem tweetListItem, @Nullable Notice notice) {
		mTweetListItem = tweetListItem;
		mNotice = notice;
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
	 * @return  The {@code comment} will be replied. Reply the {@code comment}.
	 */
	public @Nullable Comment getComment() {
		return mComment;
	}

	/**
	 *
	 * @return The notice of a comment to tweet, the {@line #mTweetListItem}.
	 */
	@Nullable
	public Notice getNotice() {
		return mNotice;
	}
}
