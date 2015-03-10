package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class Notices implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("notices")
	private List<Notice> mNotices;

	public Notices(int status, List<Notice> notices) {
		mStatus = status;
		mNotices = notices;
	}


	public int getStatus() {
		return mStatus;
	}

	public List<Notice> getNotices() {
		return mNotices;
	}

	public void setNotices(List<Notice> notices) {
		mNotices = notices;
	}
}
