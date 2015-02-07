package com.osctweet4j.ds;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class Login implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("user")
	private User mUser;

	public Login(int status, User user) {
		mStatus = status;
		mUser = user;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int status) {
		mStatus = status;
	}

	public User getUser() {
		return mUser;
	}

	public void setUser(User user) {
		mUser = user;
	}
}
