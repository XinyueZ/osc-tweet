package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class MyInformation implements Serializable{
	@SerializedName("status")
	private int mStatus;
	@SerializedName("am")
	private Am mAm;
	@SerializedName("atMe")
	private List<Notice> mNotices;
	@SerializedName("comments")
	private List<Notice> mComments;


	public MyInformation(int status, Am am, List<Notice> notices, List<Notice> comments) {
		mStatus = status;
		mAm = am;
		mNotices = notices;
		mComments = comments;
	}

	public int getStatus() {
		return mStatus;
	}

	public Am getAm() {
		return mAm;
	}

	public List<Notice> getNotices() {
		return mNotices;
	}

	public List<Notice> getComments() {
		return mComments;
	}
}
