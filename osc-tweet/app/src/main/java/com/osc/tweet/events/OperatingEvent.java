package com.osc.tweet.events;

/**
 * Event fired after operating.
 *
 * @author Xinyue Zhao
 */
public final class OperatingEvent {
	/**
	 * Success or not.
	 */
	private boolean mSuccess;

	/**
	 * Constructor of {@link OperatingEvent}
	 * @param success  Success or not.
	 */
	public OperatingEvent(boolean success) {
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
