package com.osc.tweet.events;

import com.osc4j.ds.tweet.TweetListItem;

/**
 * Event to show list of comment of a {@link com.osc4j.ds.tweet.TweetListItem}.
 *
 * @author Xinyue Zhao
 */
public final class ShowTweetCommentsListEvent {
	/**
	 * The tweet that provides comments.
	 */
	private TweetListItem mItem;

	/**
	 * Constructor {@link ShowTweetCommentsListEvent}
	 *
	 * @param item
	 * 		{@link com.osc4j.ds.tweet.TweetListItem}  that provides comments.
	 */
	public ShowTweetCommentsListEvent(TweetListItem item) {
		mItem = item;
	}


	/**
	 * @return {@link com.osc4j.ds.tweet.TweetListItem}  that provides comments.
	 */
	public TweetListItem getTweetItem() {
		return mItem;
	}
}
