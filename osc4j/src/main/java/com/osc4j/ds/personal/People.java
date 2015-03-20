package com.osc4j.ds.personal;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class People implements Serializable {
	@SerializedName("uid")
	private int mId;
	@SerializedName("name")
	private String mName;
	@SerializedName("from")
	private String mFrom;
	@SerializedName("platforms")
	private String mPlatforms;
	@SerializedName("expertise")
	private String mExpertise;
	@SerializedName("portrait")
	private String mPortrait;
	@SerializedName("gender")
	private int mGender;           //1-man, 2,famle
	@SerializedName("relation")
	private int mRelation;          //1-has been focused, 2-focused eachother, 3-no

	public People(int id, String name, String from, String platforms, String expertise, String portrait, int gender,
			int relation) {
		mId = id;
		mName = name;
		mFrom = from;
		mPlatforms = platforms;
		mExpertise = expertise;
		mPortrait = portrait;
		mGender = gender;
		mRelation = relation;
	}


	public int getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getFrom() {
		return mFrom;
	}

	public String getPlatforms() {
		return mPlatforms;
	}

	public String getExpertise() {
		return mExpertise;
	}

	public String getPortrait() {
		return mPortrait;
	}

	public Gender getGender() {
		return mGender == 1 ? Gender.Male : Gender.Female;
	}

	public int getRelation() {
		return mRelation;
	}
}
