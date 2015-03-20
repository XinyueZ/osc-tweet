package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class MyInformation implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("am")
	private People mPeople;
	@SerializedName("url")
	private Url mUrl;
	@SerializedName("atMe")
	private List<Notice> mNotices;
	@SerializedName("comments")
	private List<Notice> mComments;

	public MyInformation(int status, People people, Url url, List<Notice> notices, List<Notice> comments) {
		mStatus = status;
		mPeople = people;
		mUrl = url;
		mNotices = notices;
		mComments = comments;
	}


	public int getStatus() {
		return mStatus;
	}

	public People getPeople() {
		return mPeople;
	}

	public Url getUrl() {
		return mUrl;
	}

	public List<Notice> getNotices() {
		return mNotices;
	}

	public List<Notice> getComments() {
		return mComments;
	}
}
