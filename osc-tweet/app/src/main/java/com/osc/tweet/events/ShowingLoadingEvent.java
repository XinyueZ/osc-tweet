package com.osc.tweet.events;


public final class ShowingLoadingEvent {
	private boolean mShow;

	public ShowingLoadingEvent(boolean show) {
		mShow = show;
	}

	public boolean isShow() {
		return mShow;
	}
}
