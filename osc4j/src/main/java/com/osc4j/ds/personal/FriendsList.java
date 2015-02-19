package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class FriendsList implements Serializable {
	@SerializedName("status")
	private int mStatus;
	@SerializedName("userList")
	private List<Friend> mFriends;

	public FriendsList(int status, List<Friend> friends) {
		mStatus = status;
		mFriends = friends;
	}

	public int getStatus() {
		return mStatus;
	}


	public List<Friend> getFriends() {
		return mFriends;
	}

}
