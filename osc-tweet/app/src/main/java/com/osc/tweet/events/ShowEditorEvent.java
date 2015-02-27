package com.osc.tweet.events;

/**
 * Begin editing message event.
 *
 * @author Xinyue Zhao
 */
public final class ShowEditorEvent {
	/**
	 * Default and init message after showing the editor.
	 */
	private String mDefaultMessage;
	/**
	 * {@code true} if the default text must be used at first position of message, only if {@link #mDefaultMessage} is
	 * not empty.
	 */
	private boolean mDefaultFixed = true;

	/**
	 * Constructor of {@link ShowEditorEvent}.
	 *
	 * @param defaultMessage
	 * 		Default and init message after showing the editor.
	 * @param defaultFixed
	 * 		{@code true} if the default text must be used at first position of message, only if {@link #mDefaultMessage} is
	 * 		not empty.
	 */
	public ShowEditorEvent(String defaultMessage, boolean defaultFixed) {
		mDefaultMessage = defaultMessage;
		mDefaultFixed = defaultFixed;
	}


	/**
	 * Constructor of {@link ShowEditorEvent}.
	 *
	 * @param defaultMessage
	 * 		Default and init message after showing the editor.
	 */
	public ShowEditorEvent(String defaultMessage) {
		mDefaultMessage = defaultMessage;
	}


	/**
	 * @return Default and init message after showing the editor.
	 */
	public String getDefaultMessage() {
		return mDefaultMessage;
	}

	/**
	 * @return {@code true} if the default text must be used at first position of message, only if {@link
	 * #mDefaultMessage} is not empty. <b>Default is true</b>
	 */
	public boolean isDefaultFixed() {
		return mDefaultFixed;
	}
}
