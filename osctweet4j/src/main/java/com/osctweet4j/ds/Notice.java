package com.osctweet4j.ds;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class Notice implements Serializable {
	@SerializedName("replyCount")
	private int mReplyCount;
	@SerializedName("msgCount")
	private int msgCount;
	@SerializedName("fansCount")
	private int mFansCount;
	@SerializedName("referCount")
	private int mReferCount;

	public Notice(int replyCount, int msgCount, int fansCount, int referCount) {
		mReplyCount = replyCount;
		this.msgCount = msgCount;
		mFansCount = fansCount;
		mReferCount = referCount;
	}

	public int getReplyCount() {
		return mReplyCount;
	}

	public void setReplyCount(int replyCount) {
		mReplyCount = replyCount;
	}

	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}

	public int getFansCount() {
		return mFansCount;
	}

	public void setFansCount(int fansCount) {
		mFansCount = fansCount;
	}

	public int getReferCount() {
		return mReferCount;
	}

	public void setReferCount(int referCount) {
		mReferCount = referCount;
	}
}
