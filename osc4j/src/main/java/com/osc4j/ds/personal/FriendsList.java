package com.osc4j.ds.personal;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class FriendsList implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("friends")
	private Friends mFriends;

	public FriendsList(int status, Friends friends) {
		mStatus = status;
		mFriends = friends;
	}

	public int getStatus() {
		return mStatus;
	}

	public Friends getFriends() {
		return mFriends;
	}
}
