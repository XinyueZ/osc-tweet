package com.osc4j.ds.personal;


import com.google.gson.annotations.SerializedName;

public final class UserInformation {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("user")
	private User mUser;

	public UserInformation(int status, User user) {
		mStatus = status;
		mUser = user;
	}

	public int getStatus() {
		return mStatus;
	}

	public User getUser() {
		return mUser;
	}
}
