package com.osc4j.ds.personal;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public  class User implements Serializable{
	@SerializedName("uid")
	private int mUid;
	@SerializedName("name")
	private String mName;
	@SerializedName("ident")
	private String mIdent;
	@SerializedName("province")
	private String mProvince;
	@SerializedName("city")
	private String mCity;
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

	public User(int uid, String name, String ident, String province, String city, String platforms, String expertise,
			String portrait, int gender, int relation) {
		mUid = uid;
		mName = name;
		mIdent = ident;
		mProvince = province;
		mCity = city;
		mPlatforms = platforms;
		mExpertise = expertise;
		mPortrait = portrait;
		mGender = gender;
		mRelation = relation;
	}

	public int getUid() {
		return mUid;
	}

	public String getName() {
		return mName;
	}

	public String getIdent() {
		return mIdent;
	}

	public String getProvince() {
		return mProvince;
	}

	public String getCity() {
		return mCity;
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

	public boolean isRelated() {
		return mRelation == 1 || mRelation == 2 ;
	}

	public void setRelation(int relation) {
		mRelation = relation;
	}
}
