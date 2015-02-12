package com.osctweet4j.ds;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class User implements Serializable{
	@SerializedName("name")
	private String mName;
	@SerializedName("uid")
	private int mUid;
	@SerializedName("expired")
	private int mExpired;


	public User(String name, int uid, int expired) {
		mName = name;
		mUid = uid;
		mExpired = expired;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public int getUid() {
		return mUid;
	}

	public void setUid(int uid) {
		mUid = uid;
	}

	public int getExpired() {
		return mExpired;
	}

	public void setExpired(int expired) {
		mExpired = expired;
	}
}
