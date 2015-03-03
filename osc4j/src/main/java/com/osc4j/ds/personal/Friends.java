package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class Friends implements Serializable {
	@SerializedName("fans")
	private List<Friend> mFansList;
	@SerializedName("focus")
	private List<Friend> mFocusList;

	public Friends(List<Friend> fansList, List<Friend> focusList) {
		mFansList = fansList;
		mFocusList = focusList;
	}


	public List<Friend> getFansList() {
		return mFansList;
	}

	public List<Friend> getFocusList() {
		return mFocusList;
	}
}
