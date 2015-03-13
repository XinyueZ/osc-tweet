package com.osc.tweet.events;

/**
 * Event when the navi-drawer is opened.
 *
 * @author Xinyue Zhao
 */
public final class OpenedDrawerEvent {
	/**
	 * Which drawer, see {@link android.view.Gravity#LEFT} etc.
	 */
	private int mGravity;

	/**
	 * Constructor of {@link OpenedDrawerEvent}
	 * @param gravity Which drawer, see {@link android.view.Gravity#LEFT} etc.
	 */
	public OpenedDrawerEvent(int gravity) {
		mGravity = gravity;
	}

	/**
	 *
	 * @return Which drawer, see {@link android.view.Gravity#LEFT} etc.
	 */
	public int getGravity() {
		return mGravity;
	}
}
