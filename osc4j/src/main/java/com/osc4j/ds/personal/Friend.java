package com.osc4j.ds.personal;


import com.google.gson.annotations.SerializedName;

public final class Friend {
	@SerializedName("expertise")
	private String mExpertise;
	@SerializedName("name")
	private String mName;
	@SerializedName("userid")
	private int mUserId;
	@SerializedName("gender")
	private int mGender;
	@SerializedName("portrait")
	private String mPortrait;


	public Friend(String expertise, String name, int userId, int gender, String portrait) {
		mExpertise = expertise;
		mName = name;
		mUserId = userId;
		mGender = gender;
		mPortrait = portrait;
	}


	public String getExpertise() {
		return mExpertise;
	}

	public String getName() {
		return mName;
	}

	public int getUserId() {
		return mUserId;
	}

	public Gender getGender() {
		return mGender == 1 ? Gender.Male : Gender.Female;
	}

	public String getPortrait() {
		return mPortrait;
	}
}
