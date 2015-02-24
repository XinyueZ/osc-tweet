package com.osc.tweet.events;

/**
 * Event to show user's information.
 */
public final class ShowUserInformation {
	/**
	 * The oschina.net user-id of the user who will be shown.
	 */
	private long mUserId;

	/**
	 * Constructor of {@link ShowUserInformation}
	 *
	 * @param userId
	 * 		The oschina.net user-id of the user who will be shown.
	 */
	public ShowUserInformation(long userId) {
		mUserId = userId;
	}

	/**
	 * @return The oschina.net user-id of the user who will be shown.
	 */
	public long getUserId() {
		return mUserId;
	}
}
