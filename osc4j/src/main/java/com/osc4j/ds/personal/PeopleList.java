package com.osc4j.ds.personal;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class PeopleList implements Serializable{
	@SerializedName("status")
	private int mStatus;
	@SerializedName("people")
	private List<People> mPeople;

	public PeopleList(int status, List<People> people) {
		mStatus = status;
		mPeople = people;
	}


	public int getStatus() {
		return mStatus;
	}

	public List<People> getPeople() {
		return mPeople;
	}
}
