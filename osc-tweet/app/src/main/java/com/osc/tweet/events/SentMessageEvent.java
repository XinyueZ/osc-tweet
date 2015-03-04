package com.osc.tweet.events;

/**
 * Event fired after sending message.
 *
 * @author Xinyue Zhao
 */
public final class SentMessageEvent {
	/**
	 * Success or not.
	 */
	private boolean mSuccess;

	/**
	 * Constructor of {@link SentMessageEvent}
	 * @param success  Success or not.
	 */
	public SentMessageEvent(boolean success) {
		mSuccess = success;
	}

	/**
	 *
	 * @return  Success or not.
	 */
	public boolean isSuccess() {
		return mSuccess;
	}
}
