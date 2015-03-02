package com.osc4j.ds.personal;


import com.google.gson.annotations.SerializedName;

public final class Am extends User{
	@SerializedName("fansCount")
	private int mFansCount;
	@SerializedName("favoriteCount")
	private int mFavoriteCount;
	@SerializedName("followersCount")
	private int mFollowersCount;


	public Am(int uid, String name, String ident, String province, String city, String platforms, String expertise,
			String portrait, int gender, int relation, int fansCount, int favoriteCount, int followersCount) {
		super(uid, name, ident, province, city, platforms, expertise, portrait, gender, relation);
		mFansCount = fansCount;
		mFavoriteCount = favoriteCount;
		mFollowersCount = followersCount;
	}

	public int getFansCount() {
		return mFansCount;
	}

	public int getFavoriteCount() {
		return mFavoriteCount;
	}

	public int getFollowersCount() {
		return mFollowersCount;
	}
}
