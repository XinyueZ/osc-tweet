package com.osc.tweet.events;

import com.osc4j.ds.common.NoticeType;

/**
 * Clear list of notices.
 *
 * @author Xinyue Zhao
 */
public final class ClearNoticeEvent {
	/**
	 * The type of notice.
	 */
	private NoticeType mType;

	/**
	 * Constructor of {@link ClearNoticeEvent}.
	 *
	 * @param type
	 * 		The type of notice.
	 */
	public ClearNoticeEvent(NoticeType type) {
		mType = type;
	}

	/**
	 * @return The type of notice.
	 */
	public NoticeType getType() {
		return mType;
	}
}
