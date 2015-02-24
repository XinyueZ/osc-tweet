package com.osc.tweet.events;

/**
 * Event to show user's information.
 */
public final class ShowUserInformationEvent {
	/**
	 * The oschina.net user-id of the user who will be shown.
	 */
	private long mUserId;

	/**
	 * Constructor of {@link ShowUserInformationEvent}
	 *
	 * @param userId
	 * 		The oschina.net user-id of the user who will be shown.
	 */
	public ShowUserInformationEvent(long userId) {
		mUserId = userId;
	}

	/**
	 * @return The oschina.net user-id of the user who will be shown.
	 */
	public long getUserId() {
		return mUserId;
	}
}
