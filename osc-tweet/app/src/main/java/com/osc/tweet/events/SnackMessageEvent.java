package com.osc.tweet.events;

/**
 * A message on snack-bar bottom.
 *
 * @author Xinyue Zhao
 */
public final class SnackMessageEvent {
	/**
	 * Message to show.
	 */
	private String message;

	/**
	 * Constructor of {@link SnackMessageEvent}
	 * @param message  Message to show.
	 */
	public SnackMessageEvent(String message) {
		this.message = message;
	}

	/**
	 *
	 * @return Message to show.
	 */
	public String getMessage() {
		return message;
	}
}
