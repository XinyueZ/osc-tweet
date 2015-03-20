package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class NoRelationPeople implements Serializable{
	@SerializedName("status")
	private int mStatus;
	@SerializedName("people")
	private List<UserInformation> mPeople;

	public NoRelationPeople(int status, List<UserInformation> people) {
		mStatus = status;
		mPeople = people;
	}


	public int getStatus() {
		return mStatus;
	}

	public List<UserInformation> getPeople() {
		return mPeople;
	}
}
